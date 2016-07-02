package com.wojtechnology.sentiment

import com.wojtechnology.sentiment.actions.ActionList
import com.wojtechnology.sentiment.parse.ArgumentParser

/** Main object for SentimentAnalysis tool */
object SentimentAnalysis {

  /** Main function for program */
  def main(argsArray: Array[String]): Unit = {
    val args: Seq[String] = argsArray.slice(1, argsArray.size)
    val actions = ArgumentParser.parse(args)
    val actionList = new ActionList(actions)
    actionList.run()
  }
}