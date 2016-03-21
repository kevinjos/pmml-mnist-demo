The Demo
========

Copy the previously generated PMML to the resources directory of the main module:
```
$ cp train/MLP_MNIST.pmml evaluate/src/main/resources
```

Build the project:
```
mvn clean package
```

The build produces an executable uber-JAR file `mnist-demo-executable-1.0-SNAPSHOT.jar` (located under Apache Maven's standard target directory `target`).

Execute the example Java command-line application:
```
java -jar target/bootstrap-executable-1.0-SNAPSHOT.jar
```


JPMML-Evaluator-Bootstrap
=========================

This demo is based [JPMML-Evaluator-Bootstrap](https://github.com/jpmml/jpmml-evaluator-boostrap) written by Villu Ruusmann.

The simplest way to get started with a [JPMML-Evaluator] (https://github.com/jpmml/jpmml-evaluator) powered software project.

# Prerequisites #

* Java 1.7 or newer.
* [Apache Maven] (https://maven.apache.org/) 3.2 or newer.

# Installation and Usage #

Verify that all the requirements are met:
```
mvn -v
```

Display the list of transitive dependency JAR files that were included into this uber-JAR file:
```
mvn dependency:tree
```

# License #

This demo is licensed under the [GNU Affero General Public License (AGPL) version 3.0] (http://www.gnu.org/licenses/agpl-3.0.html).
