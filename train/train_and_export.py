#!/usr/bin/python

from mnist import MNIST
import numpy as np
from sklearn import neural_network as nn
from sklearn2pmml import sklearn2pmml


def draw_random(truth_array, prediction, test_label, test_data):
    """
    Prints the prediction, label and digit for a random misclassified sample
    """
    incorrect_idx = [idx for idx, is_true in enumerate(truth_array) if not is_true]
    n = incorrect_idx[np.random.randint(0, len(incorrect_idx))]
    print "predicted [%s]\nlabeled [%s]\nraw data:\n%s" % (prediction[n].argmax(), test_label[n], MNIST.display(test_data[n]))


def main():
    data = MNIST('./data')
    # 60,000 train samples of 28x28 grid, domain 0-255
    mnist_train_data, mnist_train_label = data.load_training()
    mnist_train_data_norm = np.array([np.array(x)/255. for x in mnist_train_data])
    # 10,000 test samples of same
    mnist_test_data, mnist_test_label = data.load_testing()
    mnist_test_data_norm = np.array([np.array(x)/255. for x in mnist_test_data])

    # Store transforms to be serialized in PMML
    # used in model training and execution
    mnist_mapper = None

    mlp_config = {'hidden_layer_sizes': (1000,),
                  'activation': 'tanh',
                  'algorithm': 'adam',
                  'max_iter': 20,
                  'early_stopping': True,
                  'validation_fraction': 0.1,
                  'verbose': True
                  }
    mnist_classifier = nn.MLPClassifier(**mlp_config)
    mnist_classifier.fit(X=mnist_train_data_norm, y=mnist_train_label)

    prediction = mnist_classifier.predict_proba(mnist_test_data_norm)
    truth_array = [prediction[idx].argmax() == mnist_test_label[idx] for idx in range(len(prediction))]
    accuracy = float(sum(truth_array)) / float(len(truth_array))
    print "out of sample model accuracy [%s]" % accuracy
    print "serializing to pmml"
    sklearn2pmml(mnist_classifier, mnist_mapper, "MLP_MNIST.pmml", with_repr=True)

if __name__ == '__main__':
    main()
