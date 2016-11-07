package com.wojtechnology.sentiment

class CommandLineOption[+T](private[this] val symbol: Symbol,
                            private[this] val value: T) {

  /**
    * @return value contained by option
    */
  def getValue: T = value
}

/**
  * Exception thrown for issues with CommandOptions
  *
  * @param message
  */
case class CommandLineOptionException(message: String) extends Exception(message)

/**
  * Stores command line options
  */
class CommandLineOptions(private[this] val optionMap: Map[Symbol, CommandLineOption[Any]]) {

  /**
    * @param optionName Symbol used to reference optin
    * @return Whether option with given name exists in map
    */
  def contains(optionName: Symbol): Boolean = {
    optionMap.contains(optionName)
  }

  /**
    * @param optionName
    * @return String value of command option
    */
  def getString(optionName: Symbol): Option[String] = {
    optionMap.get(optionName) match {
      case Some(option) => option.getValue match {
        case value: String => Some(value)
        case _ => throw CommandLineOptionException(s"$optionName is not of type 'String'")
      }
      case None => None
    }
  }

  /**
    * @param optionName
    * @return Any value of command option
    */
  def getAny(optionName: Symbol): Option[Any] = {
    optionMap.get(optionName) match {
      case Some(option) => Some(option.getValue)
      case None => None
    }
  }

}

/**
  * Parses command line arguments and returns an option object
  */
object CommandLineParser {

  val ACTION_OPTION = 'action
  val MODEL_OPTION = 'model
  val TRAIN_FILE_OPTION = 'test_file
  val EVAL_FILE_OPTION = 'eval_file
  val OUTPUT_FILE_OPTION = 'output_file

  private def parse(args: Array[String]): Option[CommandLineOptions] = {
    if (args.length >= 2) {
      type OptionMap = Map[Symbol, CommandLineOption[Any]]
      val optionMap: OptionMap = Map(
        ACTION_OPTION -> new CommandLineOption[String](ACTION_OPTION, args(0)),
        MODEL_OPTION -> new CommandLineOption[String](MODEL_OPTION, args(1))
      )

      def nextOption(map: OptionMap, argArray: List[String]): OptionMap = {
        argArray match {
          case Nil => map
          case "-t" :: trainFile :: tail => nextOption(map ++ Map(
              TRAIN_FILE_OPTION -> new CommandLineOption[String](TRAIN_FILE_OPTION, trainFile)
          ), tail)
          case "-e" :: evalFile :: tail => nextOption(map ++ Map(
              EVAL_FILE_OPTION -> new CommandLineOption[String](EVAL_FILE_OPTION, evalFile)
          ), tail)
          case "-o" :: outputFile :: tail => nextOption(map ++ Map(
            OUTPUT_FILE_OPTION -> new CommandLineOption[String](OUTPUT_FILE_OPTION, outputFile)
          ), tail)
          case option :: tail => {
            println(s"Unknown option '$option'")
            nextOption(map, tail)
          }
        }
      }

      Some(new CommandLineOptions(nextOption(optionMap, args.drop(2).toList)))
    } else {
      println("Too little arguments")
      None
    }
  }

  /**
    * Parses and validates arguments
    *
    * @param args
    * @return Some of options if args are valid, else None
    */
  def parseAndValidate(args: Array[String]): Option[CommandLineOptions] = {
    parse(args) match {
      case Some(options) => if (validate(options)) Some(options) else None
      case None => None
    }
  }

  /**
    * Validates options
    *
    * @param options
    * @return Some of options if args are valid, else None
    */
  private def validate(options: CommandLineOptions): Boolean = {
    (options.getString(MODEL_OPTION), options.getString(ACTION_OPTION)) match {
      case (Some("tfidf"), Some("fit")) => {
        assertContains(options, Set(TRAIN_FILE_OPTION, OUTPUT_FILE_OPTION))
      }
      case (Some(model), Some(action)) => {
        // TODO(wojtek): Check if model and action are valid
        println(s"'$action' action is not defined for model '$model'")
        false
      }
      case _ => {
        // NB(wojtek): This should never take place
        false
      }
    }
  }


  /**
    * Asserts that `options` contain CommandOption with given symbols
    *
    * @param options
    * @param symbols
    * @return Whether `options` contains the required symbols
    */
  private def assertContains(options: CommandLineOptions, symbols: Set[Symbol]): Boolean = {
    symbols.foldLeft(true)((last: Boolean, symbol: Symbol) => options.contains(symbol) && last)
  }

  /**
    * Prints correct usage of program
    */
  def printUsage = {
    println("Usage: sentimentanalysis.jar <action> <model> [args] [options]")
    println("Actions: 'fit', 'predict'")
    println("Models: 'tfidf'")
  }
}
