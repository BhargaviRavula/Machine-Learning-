Naive Bayes:

With StopWords:

Compilation:

javac NaiveBayesTrain.java

Running:

java NaiveBayesTrain train/ham train/spam test/ham test/spam


Without StopWords:

Compilation:

javac NaiveBayesTrainWOStopWords.java

Running:

java NaiveBayesTrainWOStopWords train/ham train/spam test/ham test/spam stopwords.txt


Logistic Regression:

With StopWords:

Compilation:

javac LogisticRegressionTrain.java

Running:

java LogisticRegressionTrain 0.001 0.05 200 train/ham train/spam test/ham test/spam


Without StopWords:

Compilation:

javac LogisticRegressionTrainWOStopWords.java

Running:

java LogisticRegressionTrainWOStopWords 0.001 0.05 200 train/ham train/spam test/ham test/spam stopwords.txt




