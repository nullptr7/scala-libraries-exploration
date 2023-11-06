package com.github.nullptr7

object ScoptDemo {
  private case class Config(
      foo: Int = -1
    )

  private case class FlagConfig(flag1: Boolean = true, flag2: Boolean = true)

  import scopt.OParser

  private val builder = OParser.builder[Config]

  private val parser1 = {
    import builder._
    OParser.sequence(
      programName("scopt"),
      head("scopt", "4.x"),
      // option -f, --foo
      opt[Int]('f', "foo")
        //.action((x, c) => c.copy(foo = x))
        .text("foo is an integer property"),
    )
  }

  private val flagBuilder = OParser.builder[FlagConfig]

  private val flagParser = {
    import flagBuilder._
    OParser.sequence(
      programName("Flag Parser"),
      head("Flag Reader", "1.0"),
      opt[Boolean]("flag1")
        .action((x, c) => c.copy(flag1 = x)),
      opt[Boolean]("flag2")
        .action((x, c) => c.copy(flag2 = x)),
      help("help").text("prints this usage text"),
    )
  }

  def main(args: Array[String]): Unit =
    // OParser.parse returns Option[Config]
    /*OParser.parse(parser1, List("--help"), Config()) match {
      case Some(config) =>
      println(config)
      case _            =>
        println("bad")
      // arguments are bad, error message will have been displayed
    }*/
    OParser
      .parse(flagParser, List("--flag1", "false", "--flag2", "true"), FlagConfig()) match {
      case Some(value) => println(s"Getting the config $value")
      case None        => println("nothing here")
    }

    OParser.parse(parser1, List("--foo", "9"), Config()) match {
      case Some(value) => println(s"Getting the config $value")
      case None        => println("nothing here")
    }

}
