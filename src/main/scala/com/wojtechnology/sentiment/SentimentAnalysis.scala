package com.wojtechnology.sentiment

import org.apache.log4j.LogManager
import org.apache.spark.sql.SparkSession


/** Main object for SentimentAnalysis tool */
object SentimentAnalysis {
  val log = LogManager.getLogger("com.wojtechnology.sentiment")

  /** Main function for program */
  def main(args: Array[String]) {
    CommandLineParser.parseAndValidate(args) match {
      case Some(options) => {
        val spark = initSpark("SentimentAnalysis")

        (options.getString(CommandLineParser.MODEL_OPTION),
            options.getString(CommandLineParser.ACTION_OPTION)) match {
          case (Some("tfidf"), Some("fit")) => {
            TfidfPredictor.train(options, spark)
          }
          case (Some("naive"), Some("fit")) => {
            NaiveBayesPredictor.train(options, spark)
          }
          case (Some("naive"), Some("eval")) => {
            NaiveBayesPredictor.eval(options, spark)
          }
          case _ =>
            // NB(wojtek) if this happens, it means that parseAndValidate is inconsistent with this
            // piece of code.
            assert(assertion = false,
              "if this happens, it means that parseAndValidate " +
                "is inconsistent with this piece of code.")
        }
      }
      case None => {
        CommandLineParser.printUsage
        System.exit(1)
      }
    }
  }

  /**
    * Init SparkContext with given app name
    *
    * @param appName Name of Spark Application
    * @return SparkContext for use in program
    */
  def initSpark(appName: String): SparkSession = {
    SparkSession.builder().appName(appName).getOrCreate()
  }
}
