# SentimentAnalysis

## Purpose
To learn how to train a model for sentiment analysis

## Links
https://github.com/caesar0301/awesome-public-datasets

http://help.sentiment140.com/for-students/

http://cs.stanford.edu/people/alecmgo/papers/TwitterDistantSupervision09.pdf

# Dependencies
As of now, this requires that you have Spark 1.6.2 installed and all paths set correctly :). Also, it is only tested on Mac OSX, so I don't know if it works elsewhere.

## How to run
```
// From root folder
spark-submit \
    --class com.wojtechnology.sentiment.SentimentAnalysis \
    --master local[4] \
    target/scala-2.11/sentimentanalysis_2.11-1.0.jar \
    data/training.1600000.processed.noemoticon.csv
```
