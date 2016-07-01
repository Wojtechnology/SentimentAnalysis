package com.wojtechnology.sentiment.actions

class InvalidParamsException extends Exception

/**
  * Abstract base class for program actions
  *
  * Actions can have dependencies (parent actions) that are required before they are
  * executed
  */
abstract class Action(protected[this] val args: Seq[String]) {

  // Validates that params are correct
  protected[this] def validateArgs()

  // Exectutes action
  protected[this] def execute()

  def run() = {
    validateArgs()
    execute()
  }
}
