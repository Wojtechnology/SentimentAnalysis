package com.wojtechnology.sentiment.actions

/**
  * Created by wojtekswiderski on 2016-06-10.
  */
class OpenFile(private[this] val args: Seq[String]) extends Action(args) {

  // Explicitly stating that this action has no required dependencies
  override val requiredDependencies = {}

  override def validateArgs() = {
    if (args.length != 1) {
      throw new InvalidParamsException
    }
  }

  override def execute() = {
    val fileName = args(0)
    println(s"OpenFile executed with file: $fileName")
  }

}
