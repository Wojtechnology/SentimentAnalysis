package com.wojtechnology.sentiment.actions

/**
  * Created by wojtekswiderski on 2016-06-10.
  */
class OpenFile extends Action {

  // Explicitly stating that this actions has no required dependencies
  override val requiredDependencies = {}

  override def validateParams() = {
    if (params.length != 1) {
      throw new InvalidParamsException
    }
  }

  override def execute() = {
    val fileName = params(0)
    println(s"OpenFile executed with file: $fileName")
  }

}
