package com.wojtechnology.sentiment

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
      val logData = sc.textFile(fileName, 2).cache()
      val numAs = logData.filter(line => line.contains("a")).count()
      val numBs = logData.filter(line => line.contains("b")).count()
      println(s"Lines with a: $numAs, Lines with b: $numBs")
    }
  }
}