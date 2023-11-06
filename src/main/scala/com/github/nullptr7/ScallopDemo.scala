package com.github.nullptr7

import org.rogach.scallop._

object ScallopDemo {
  private class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
    case class ReturnConf(
        high: Int,
        low:  Int,
      )

    private val high1: ScallopOption[Int] = opt[Int](name = "high")
    private val low1:  ScallopOption[Int] = opt[Int](name = "low")

    val giveThis: ScallopOption[ReturnConf] = for {
      high <- high1
      low  <- low1
    } yield ReturnConf(high, low)

    verify()

  }

  def main(args: Array[String]): Unit = {
    val conf = new Conf(List("--high", "1", "--low", "0"))

    println(conf.giveThis)
  }

}
