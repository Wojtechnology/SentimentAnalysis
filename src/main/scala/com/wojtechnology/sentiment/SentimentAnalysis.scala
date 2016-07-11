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
      val sc = initSpark("SentimentAnalysis")

      val parser = new CSVParser with ColumnsFilter

      val rawData = sc.textFile(fileName)
      val parsed = parser.parse(rawData)
      val filtered = parser.filter(parsed, Seq(0,3,4,5))
      val hasSmiley = filtered.filter(_(3).contains(":("))
      val countSmiley = hasSmiley.count()

      println(countSmiley)
      hasSmiley.foreach(println)
      // filtered.take(10).foreach(println)
    }
  }

  /**
    * Init SparkContext with given app name
    * @param appName
    * @return SparkContext for use in program
    */
  def initSpark(appName: String): SparkContext = {
    val conf = new SparkConf().setAppName(appName)
    new SparkContext(conf)
  }
}