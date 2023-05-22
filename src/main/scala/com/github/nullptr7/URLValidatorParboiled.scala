package com.github.nullptr7

import org.parboiled2._

object URLValidatorParboiled extends App {

  private final case class AllValidUrls(urls: Seq[Map[String, String]])

  private val aValidUrl       =
    """
      |prefix aValidUrl : <https://www.google.com>
      |prefix aValidUrl : <https://www.google.com>
      |prefix aValidUrl : <https://www.google.com>
      |prefix aValidUrl : <https://www.google.com>
      |
      |rule someRuleName
      |when
      | Something($data)
      |then
      | doThis($message)
      |end
      |""".stripMargin

  private val anInvalidUrl    = "prefix anInvalidUrl : <https://>"
  private val anInvalidFormat = "prfix aValidUrl: <https://www.google.com>"
  private val insecureUrl     = "prefix insecureUrl: <http://www.google.com>"

  private class UrlValidator(override val input: ParserInput) extends Parser {
    private val NoNewLineWhiteSpaceChar: CharPredicate = CharPredicate(" \t\f")
    private val NewLineChar:             CharPredicate = CharPredicate("\n\r")
    private val WhiteSpaceChar:          CharPredicate = NoNewLineWhiteSpaceChar ++ NewLineChar

    private val JavaIdentifierPart = CharPredicate.from(ch => Character.isJavaIdentifierPart(ch))

    def InputLine = rule {
      oneOrMore(start).separatedBy(WhiteSpace) ~ WhiteSpace ~ EOI ~> AllValidUrls
    }

    private def start = rule {
      zeroOrMore(NewLineChar) ~ "prefix" ~ atLeastOneWhiteSpaceChar ~ Identifier ~ ws(":") ~ Uri ~> {
        (iden, uri) => push(Map(iden -> uri))
      }
    }

    private def atLeastOneWhiteSpaceChar: Rule0 = rule {
      oneOrMore(CharPredicate(" "))
    }

    private def Number = rule {
      capture(oneOrMore(CharPredicate.Digit)) ~> (_.toInt)
    }

    private def Identifier = rule {
      capture(CharPredicate.Alpha ~ oneOrMore(JavaIdentifierPart)) ~> { str =>
        test(str != "scott") ~ push(str)
      }
    }

    private def ws(s: String) = rule {
      WhiteSpaceChar ~ s ~ WhiteSpaceChar
    }

    private def Uri =
      rule {
        '<' ~ capture(oneOrMore(!'>' ~ ANY)) ~ '>' ~> { str =>
          if (str.indexOf(":") == -1) {
            println(s"okay $str")
            str
          }
          else {
            println(s"okay1 $str")
            str
          }
        }
      }

    private def WhiteSpace: Rule0 = rule {
      zeroOrMore(WhiteSpaceChar)
    }

  }

  println(new UrlValidator(aValidUrl).InputLine.run())

}
