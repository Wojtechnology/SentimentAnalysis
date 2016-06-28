package com.wojtechnology.sentiment.actions

/**
  * List of actions
  *
  * This is the class used to run a list of actions in the correct order
  * such that all dependent actions occur before their children
  */
class ActionList(private[this] val actions: Seq[Action]) {

  // Populates children for all actions currently in the action list
  def populateChildren() = {

  }

  def run() = {
    topologicalSort()
  }

  private def topologicalSort() = {

  }
}
