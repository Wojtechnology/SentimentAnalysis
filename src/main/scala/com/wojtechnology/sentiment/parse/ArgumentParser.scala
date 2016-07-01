package com.wojtechnology.sentiment.parse

import com.wojtechnology.sentiment.actions._

/**
  * Created by wojtekswiderski on 2016-06-10.
  */
object ArgumentParser {
  private[this] final val FILE_INPUT = "-f"

  private[this] final val OPTIONS = Set(FILE_INPUT)

  private[this] def createAction(option: String, args: Seq[String]): Action = {
    option match {
      case FILE_INPUT => new FileStreamAction(args)
    }
  }

  def parse(args: Seq[String]): ActionList = {
    // Find indices of starts of commands
    val idxs = args.zipWithIndex.foldLeft(Seq[Int](0)) { (acc, arg) =>
      arg._1 match {
        case x if OPTIONS contains x => acc :+ arg._2
        case _ => acc
      }
    }
    // Create action list from options
    new ActionList(idxs.zipWithIndex.map{
      case (x, y) => createAction(args(x), args.slice(x+1,
        if (y + 1 < idxs.size) idxs(y+1) // Upper bound is next option
        else args.size)) // For last argument, this is size of arguments
    })
  }
}
