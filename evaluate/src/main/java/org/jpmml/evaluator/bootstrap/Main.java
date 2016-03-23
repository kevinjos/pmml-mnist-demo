package org.jpmml.evaluator.bootstrap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import com.google.gson.JsonParser;
import org.jpmml.evaluator.Computable;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.model.ImportFilter;
import org.jpmml.model.JAXBUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.post;
import static spark.Spark.get;

public class Main {

	static
	public void main(String... args) throws Exception {
		PMML pmml;

		String modelFileName = "MLP_MNIST.pmml";

		InputStream is = Main.class.getClassLoader().getResourceAsStream(modelFileName);
		System.out.println("Loading resource [" + modelFileName + "]");
		try {
			Source source = ImportFilter.apply(new InputSource(is));
			try {
				pmml = JAXBUtil.unmarshalPMML(source);
			} catch (JAXBException e) {
				System.out.println("for source [" + source.toString() + "]: " + e.toString());
				return;
			}
		} catch (SAXException e) {
			System.out.println("for input stream [" + is + "]: " + e.toString());
			return;
		}

		ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();

		Evaluator evaluator = modelEvaluatorFactory.newModelManager(pmml);

		get("/info", (req, res) -> {
			String response = "<br>" + "Mining function: " + evaluator.getMiningFunction() + "</br>";
			response += "<br>" + "Input schema:" + "</br>";
			response += "<br>" + "Active fields: " + evaluator.getActiveFields() + "</br>";
			response += "<br>" + "Group fields: " + evaluator.getGroupFields() + "</br>";
			response += "<br>" + "Output schema:" + "</br>";
			response += "<br>" + "Target fields: " + evaluator.getTargetFields() + "</br>";
			response += "<br>" + "Output fields: " + evaluator.getOutputFields() + "</br>";
			return response;
		});

		post("/digit", (req, res) -> {
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(req.body()).getAsJsonObject();
			JsonArray dataArray = json.getAsJsonArray("data");
			Map<FieldName, Double> input = new HashMap<>();
			int idx = 0;
			List<FieldName> fieldNames = evaluator.getActiveFields();
			for (FieldName field : fieldNames) {
				input.put(field, dataArray.get(idx).getAsDouble());
				idx++;
			}
			Map<FieldName, ?> output;
			output = evaluator.evaluate(input);
			Computable targetValue = (Computable) output.get(evaluator.getTargetField());
			return targetValue.getResult().toString();
		});
	}

}