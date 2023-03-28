package com.github.nullptr7

import eu.timepit.refined._
import eu.timepit.refined.api.{ Refined, RefType }
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean._
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._
import eu.timepit.refined.string._
import scala.util.Random

object RefinedExampleADT extends App {
  object refined {
    //  private type Name = Refined[String, NonEmpty]
    type Name             = String Refined NonEmpty
    private type AgeRange = Not[Less[18]] And Not[Greater[99]]
    type Age              = Int Refined AgeRange
    type Salary           = Double Refined Positive

    type Username      = String Refined NonEmpty
    type Password      = String Refined (NonEmpty And MaxSize[20] And MinSize[5])
    type ConnectionUrl = String Refined
      MatchesRegex[
        W.`"""^((http|https)://)[-a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)$"""`.T
      ]

  }

  import refined._

  private case class Employee(
      name:   Name,
      age:    Age,
      salary: Salary,
    )

  private case class DatabaseConnection(
      username:      Username,
      password:      Password,
      connectionUrl: ConnectionUrl,
    )

  private def createEmployeeHardCoded: Employee =
    Employee("Scott", 25, 10.0)

  private def createDbConnectionHardCoded: DatabaseConnection =
    DatabaseConnection("scott", "tiger", "https://database.connection.io")

  /*private def createEmployeeDynamic(name: String, age: Int, salary: Double): Employee = {

    for {
      n <- RefType.applyRef[Name](name)
    } yield ()


    Employee(name, age, salary)
  }*/

}
