package com.wojtechnology.sentiment.actions

/**
  * Enum for the different action types that exist
  */
object ActionType extends Enumeration {
  type ActionType = Value
  val OpenFileAction = ActionType
}

class InvalidParamsException extends Exception

/**
  * Abstract base class for program actions
  *
  * Actions can have dependencies (parent actions) that are required before they are
  * executed
  */
abstract class Action() {
  // Actions required to be executed before this actions
  protected[this] val requiredDependencies = {}

  // Actions that will be executed (in any order) some time after this action
  protected[this] val children: scala.collection.mutable.ArrayBuffer[Action] =
    scala.collection.mutable.ArrayBuffer[Action]()

  protected[this] val params: scala.collection.mutable.ArrayBuffer[String] =
    scala.collection.mutable.ArrayBuffer[String]()

  // Validates that params are correct
  protected[this] def validateParams()

  // Exectutes action
  protected[this] def execute()

  def run() = {
    validateParams()
    execute()
  }

  // Mutator - adds child to children
  def addChild(action: Action) = {
    children.append(action)
  }

  // Accessor - returns list of children
  def getChildren(): scala.collection.mutable.ArrayBuffer[Action] = {
    children
  }

  // Mutator - adds param to params
  def addParam(param: String) = {
    params.append(param)
  }
}
