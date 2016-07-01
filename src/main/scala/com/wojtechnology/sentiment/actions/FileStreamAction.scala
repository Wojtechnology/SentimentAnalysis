package com.wojtechnology.sentiment.actions

/**
  * Created by wojtekswiderski on 2016-06-10.
  */
class FileStreamAction(private[this] val args: Seq[String]) extends Action(args) {

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
