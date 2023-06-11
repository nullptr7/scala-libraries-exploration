package com.github.nullptr7

import play.api.libs.json.Format.GenericFormat
import play.api.libs.json._
import shapeless._
import shapeless.syntax.singleton._
import shapeless.tag._

import scala.language.implicitConversions

object TypeTagging extends App {
  sealed private trait Username
  sealed private trait Password

  private case class LoginDetails(username: Username @@ String, password: Password @@ String)

  private val someNumber    = 42
  private val numOfCherries = "numCherries" ->> someNumber

  private val numOfCherries2 = someNumber.asInstanceOf[Int with String]

  println(numOfCherries.getClass == numOfCherries2.getClass)

  private object tags {
    sealed trait CardNumber

    sealed trait CVV

  }

//  private case class CardNumber(value: String) extends AnyVal
//  private case class CVV(value: String) extends AnyVal

  import tags._
  private case class CreditCard(
      number: String @@ CardNumber,
      holder: String,
      cvv:    String @@ CVV,
    )

  implicit private lazy val readsCardNumber: Reads[String @@ CardNumber] =
    __.read[String]
      .map(_.replaceAll("\\s", ""))
      .collect(JsonValidationError("Invalid card number")) {
        case num if (16 to 19).contains(num.length) =>
          tag[CardNumber](num)
      }

  implicit private lazy val readsCVV: Reads[String @@ CVV] =
    __.read[String](
      Reads.pattern("\\d{3,5}".r, "Invalid CVV")
    ).map(tag[CVV](_))

  implicit private lazy val cardFormat: OFormat[CreditCard] = Json.format[CreditCard]

  private val validCard: JsObject = Json.obj(
    "number" -> "1111 2222 3333 4444",
    "holder" -> "CARD HOLDER",
    "cvv"    -> "666",
  )

  private val malformedNumberCard = validCard + ("number" -> JsString("111"))
  private val malformedCVVCard    = validCard + ("cvv"    -> JsString("6 6 6"))

  println(Json.fromJson[CreditCard](validCard))           // JsSuccess
  println(Json.fromJson[CreditCard](malformedNumberCard)) // Invalid card
  // number
  println(Json.fromJson[CreditCard](malformedCVVCard))    // Invalid CVV

  implicit private def toCardNumber(value: String): String @@ CardNumber =
    tag[CardNumber](value)

  implicit private def toCvvNumber(value: String): String @@ CVV =
    tag[CVV](value)

  private val aCreditCard: CreditCard = CreditCard(
    number = "123 456 789",
    holder = "Ishan Shah",
    cvv    = "123",
  )

  println(aCreditCard)

}
