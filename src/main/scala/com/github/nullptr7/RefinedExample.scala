package com.github.nullptr7

import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._
import eu.timepit.refined.api.{Refined, RefType}
import eu.timepit.refined.boolean._
import eu.timepit.refined.char._
import eu.timepit.refined.collection._
import eu.timepit.refined.generic._
import eu.timepit.refined.string._
import scala.util.Random
import shapeless.{::, HNil}

object RefinedExample extends App {
  private val aPositiveInteger: Int Refined Positive = 5

  private val anIntegerGreaterThanTen: Int Refined Greater[10] = 15

  println(aPositiveInteger)
  println(anIntegerGreaterThanTen)

  private type ZeroToOne = Not[Less[0.0]] And Not[Greater[1.0]]

  private val knot2: Refined[Double, ZeroToOne] = refineMV[ZeroToOne](0.2)

  println(knot2)

  private type AnyOf1 = AnyOf[Digit :: Letter :: Whitespace :: HNil]

  private def randomlySelectSomething: Char = {
    val aList  = List('@', '1', ' ', 'P')
    def random = Random.between(0, 3)
    aList(random)
  }

//  private val anyof1 = RefType.applyRef[AnyOf1](' ')

  private type Email = String Refined MatchesRegex[W.`"""[a-z0-9]+@[a-z0-9]+\\.[a-z0-9]{2,}"""`.T]

  private val poorEmail = "daniel"
  private val refineCheck = RefType.applyRef[Email](poorEmail)

  println(refineCheck)

}
