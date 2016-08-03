package com.wojtechnology.sentiment

import com.wojtechnology.sentiment.parse.{CSVParser, ColumnsFilter}
import org.apache.spark.rdd.RDD
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

      countTokens(sc.textFile(fileName), ":(")
    }
  }

  /**
    * Init SparkContext with given app name
    * @param appName Name of Spark Application
    * @return SparkContext for use in program
    */
  def initSpark(appName: String): SparkContext = {
    val conf = new SparkConf().setAppName(appName)
    new SparkContext(conf)
  }

  /**
    * Counts number of rows in data containing token and print first 10
    * @param rawData Raw file data
    * @param token token to count
    * @return Unit
    */
  def countTokens(rawData: RDD[String], token: String) = {
    val parser = new CSVParser with ColumnsFilter

    val parsed = parser.parse(rawData)
    val filtered = parser.filter(parsed, Seq(5))
    val hasSmiley = filtered.filter(_.head.contains(token))
    val countSmiley = hasSmiley.count()

    println(s"Found $countSmiley smileys!")
    hasSmiley.foreach(println)
  }
}