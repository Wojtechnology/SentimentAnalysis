package com.wojtechnology.sentiment.actions

/**
  * List of actions
  *
  * This is the class used to run a list of actions in the correct order
  * such that all dependent actions occur before their children
  */
class ActionList(private[this] val actions: Seq[Action]) {

  // Sorts actions to run based on dependency and runs actions
  def run() = {
    topologicalSort()
    actions.foreach(_.run)
  }

  private def topologicalSort() = {
    // TODO: Implement plz
  }
}
