package com.github.nullptr7

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random
import zio._

object LongComputationProcessZStreams /*extends ZIOAppDefault*/ extends App {
  case class Person(name: String, age: Int)

  private val aLotOfPersons =
    List.fill(100)(Person(Random.alphanumeric.take(10).mkString, Random.nextInt(100)))

  import zio.stream._

  private val streams = ZStream
    .fromIterable(aLotOfPersons)
    .mapZIOParUnordered(10)(p => ZIO.from(longOp(p)))
    .runCollect
    .flatMap(c => ZIO.succeed(c.toList))
    .tap(x => Console.printLine(x))

  private def longOp(person: Person): Future[Person] =
    for {
      _    <- Future.successful(println(s"${Thread.currentThread().getName}"))
      resp <- Future {
                Thread.sleep(1000)
                person
              }
//      _    <- Future.successful(println(s"${Thread.currentThread().getName}"))
    } yield resp

  val runtime = Runtime.default

  Unsafe.unsafe { implicit unsafe =>
    runtime.unsafe.run(streams)
  }

//  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = streams

}
