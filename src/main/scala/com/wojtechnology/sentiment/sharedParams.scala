package com.wojtechnology.sentiment

import org.apache.spark.ml.param.{Param, Params}


/**
  * Trait to use for transformers that use an input column
  */
private[sentiment] trait HasInputCol extends Params {

  final val inputCol: Param[String] = new Param[String](this, "inputCol", "input column name")

  /**
    * @return String value of input column
    */
  final def getInputCol: String = $(inputCol)

}


/**
  * Trait to use for transformers that use an output column
  */
private[sentiment] trait HasOutputCol extends Params {

  final val outputCol: Param[String] = new Param[String](this, "outputCol", "output column name")

  /**
    * @return String value of output column
    */
  final def getOutputCol: String = $(outputCol)

}
