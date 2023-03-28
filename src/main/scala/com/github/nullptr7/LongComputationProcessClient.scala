package com.github.nullptr7

import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.kernel.Resource
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.implicits.http4sLiteralsSyntax
import org.typelevel.ci.CIString
import scala.language.postfixOps
import org.http4s.client.blaze._
import org.http4s.client.Client

object LongComputationProcessClient extends IOApp.Simple {
  case class Person(name: String, age: Int)
  implicit val personDecoder: Decoder[Person] = deriveDecoder[Person]

  val clientRes: Resource[IO, Client[IO]] = BlazeClientBuilder[IO].withMaxTotalConnections(50).resource


  val request = Request[IO](uri = uri"http://localhost:8000/long-computation/20")
//    .withHeaders(Header.Raw(CIString("Accept"), "application/x-ndjson"))
    .withHeaders(Header.Raw(CIString("Accept"), "text/event-stream"))

  val response = clientRes.use {
    _.run(request).use {
      _.body
        .through(fs2.text.utf8.decode)
        .map(io.circe.jawn.decode[Person])
        .compile
        .toList

//        .evalMap(line => IO(io.circe.jawn.decode[Person](line)))
//        .evalTap(IO.println)
//        .compile
//        .toList
    }

  }

  override def run: IO[Unit] = response.map(println) *> IO.unit

}
