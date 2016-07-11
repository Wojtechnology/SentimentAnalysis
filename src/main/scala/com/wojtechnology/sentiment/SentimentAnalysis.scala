package com.wojtechnology.sentiment

import com.wojtechnology.sentiment.parse.{CSVParser, ColumnsFilter}
import org.apache.spark.{SparkConf, SparkContext}

/** Main object for SentimentAnalysis tool */
object SentimentAnalysis {

  /** Main function for program */
  def main(args: Array[String]) {
    if (args.length < 1) {
      println("Did not provide a filename")
    } else {
      val fileName = args.head
      val conf = new SparkConf().setAppName("SentimentAnalysis")
      val sc = new SparkContext(conf)
      val parser = new CSVParser with ColumnsFilter

      val rawData = sc.textFile(fileName)
      val filtered = parser.parse(rawData)
      filtered.take(10).foreach(println)
    }
  }
}