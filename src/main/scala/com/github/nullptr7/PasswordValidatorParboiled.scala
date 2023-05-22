package com.github.nullptr7

import org.parboiled2._
import org.parboiled2.support.hlist
import org.parboiled2.support.hlist.HNil

private object PasswordValidatorParboiled extends App {
  final private val passwordDecipheredString =
    "DECIPHERED\n(h7pPaVopQb5lz3WhVbqdmRTIYIRw2nnv7j4Lnzsy5dJ2)"

  final private class PasswordValidator(override val input: ParserInput) extends Parser {
    private val NewLineChar: CharPredicate = CharPredicate("\n\r")

    private val AlphaNumericChars: CharPredicate = CharPredicate.AlphaNum

    def parserStart: Rule1[String] = rule {
      "DECIPHERED" ~ NewLineChar ~ '(' ~ capture(oneOrMore(AlphaNumericChars)) ~ ')'
    }

  }

  println(new PasswordValidator(passwordDecipheredString).parserStart.run())

}
