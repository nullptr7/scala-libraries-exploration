package com.github.nullptr7

import cats.effect.{ IO, IOApp }
import fs2.Stream
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import scala.collection.mutable.ListBuffer
import scala.util.Random
//import io.circe.generic.auto._
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
//import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.io._
import org.http4s.implicits._
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object LongComputationProcess extends IOApp.Simple {
  case class Person(name: String, age: Int)
  private case class PersonRequestBody(name: String, age: String)

  implicit val personEncoder: Encoder[Person] = deriveEncoder[Person]

  private def longComputation(batch: List[PersonRequestBody]) =
    Stream
      .emits(batch)
      .covary[IO]
      .parEvalMap(5) { x =>
        IO.sleep(1.second) *> IO.pure(Person(x.name, x.age.toInt))
      }

  private def generateRandomPersonRequest(quantity: Int): List[PersonRequestBody] = {
    val mutList = scala.collection.mutable.ListBuffer.empty[PersonRequestBody]
    for (_ <- 1 to quantity)
      mutList.append(PersonRequestBody(Random.nextString(10), Random.nextInt(100).toString))
    mutList.toList
  }

  private def longComputation: Stream[IO, String] =
    Stream
      .emits(List("Result 1", "Result 2", "Result 3"))
      .mapAsync[IO, String](1)(_ => IO.sleep(2 seconds).as("Next result"))

  val tickPersons =
    Stream.awakeEvery[IO](1.second).map(_ => Person(Random.nextString(10), Random.nextInt(100)))

  val seconds = Stream.awakeEvery[IO](1.second).map(_.toString())

  val clientRes = BlazeClientBuilder[IO].resource

  val request = Request[IO](uri = uri"http://localhost:8000/long-computation")

  val response = clientRes.use { client =>
    client.run(request).use { resp =>
      resp
        .body
        .through(fs2.text.utf8.decode)
        .compile
        .string
        .map(body => println(s"Received response: ${body}"))
    }

  }

  private val app = HttpRoutes
    .of[IO] {
      case GET -> Root / "long-computation" / IntVar(quantity) =>
        Ok(longComputation(generateRandomPersonRequest(quantity)))
      /*Ok(
          IO(
            Response[IO](
              body    = longComputation,
              headers = Headers.apply(Header.Raw(CIString("Content-Type"), "text/event-stream")),
            )
          )
        )*/
    }
    .orNotFound

  override def run: IO[Unit] =
    BlazeServerBuilder[IO]
      .bindHttp(8000, "localhost")
      .withHttpApp(app)
      .resource
      .use(_ => IO.never[Unit])

}
