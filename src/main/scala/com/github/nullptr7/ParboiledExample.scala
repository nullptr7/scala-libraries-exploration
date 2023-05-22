package com.github.nullptr7

import org.parboiled2._

object ParboiledExample extends App {
  private class Calculator(val input: ParserInput) extends Parser {
    def InputLine = rule {
      Expression ~ EOI
    }

    private def Expression: Rule1[Int] = rule {
      Term ~ zeroOrMore(
        '+' ~ Term ~> ((_: Int) + _)
          | '-' ~ Term ~> ((_: Int) - _)
      )
    }

    private def Term = rule {
      Factor ~ zeroOrMore(
        '*' ~ Factor ~> ((_: Int) * _)
          | '/' ~ Factor ~> ((_: Int) / _)
      )
    }

    private def Factor = rule {
      Number | CurlyParens
    }

    private def CurlyParens = rule {
      '{' ~ Parens ~ '}'
    }

    private def Parens = rule {
      '(' ~ Expression ~ ')'
    }

    private def Number = rule {
      capture(Digits) ~> (_.toInt)
    }

    private def Digits = rule {
      oneOrMore(CharPredicate.Digit)
    }

  }

  println(new Calculator("1-10*{(2-2)}").InputLine.run())

}
