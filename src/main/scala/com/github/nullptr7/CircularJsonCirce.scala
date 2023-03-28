package com.github.nullptr7

import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator

object CircularJsonCirce extends App {
  import io.circe.parser.decode
  import io.circe.{ Decoder, HCursor }
  import io.circe.generic.auto._

  case class Person(
      name:   String,
      age:    Int,
      parent: Option[Person] = None,
    )

  object Person {
    implicit val personDecoder: Decoder[Person] = (c: HCursor) =>
      for {
        name   <- c.downField("name").as[String]
        age    <- c.downField("age").as[Int]
        parent <- c.downField("parent").as[Option[Person]](Decoder.decodeOption(personDecoder))
      } yield Person(name, age, parent)

  }

  val jsonString =
    """{
      |  "name": "John",
      |  "age": 30,
      |  "parent": {
      |    "name": "Jane",
      |    "age": 60,
      |    "parent": {
      |      "name": "Bob",
      |      "age": 90,
      |      "parent": {
      |        "name": "John",
      |        "age": 120
      |      }
      |    }
      |  }
      |}""".stripMargin

  val result = decode[Person](jsonString)


  import com.fasterxml.jackson.databind.ObjectMapper
  import com.fasterxml.jackson.module.scala.DefaultScalaModule
  import com.fasterxml.jackson.module.scala.ScalaObjectMapper

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.activateDefaultTyping(mapper.getDeserializationConfig.getPolymorphicTypeValidator)


  def fromJson(json: String): Person = mapper.readValue[Person](json)

  result match {
    case Right(person) => println(s"Deserialized person: $person")
    case Left(error)   => println(s"Error decoding JSON: $error")
  }

  println(fromJson(jsonString))
}
