package com.github.nullptr7

import java.io.IOException
import java.util.UUID
import zhttp.http._
import zhttp.service.Server
import zio._
import zio.stream.ZStream
import zio.Console.printLine

object ZIOStreamExamples extends ZIOAppDefault {
  final private case class ProcessId(value: String) extends AnyVal

  private def server(queue: Queue[ProcessId]): ZIO[Any, Throwable, Nothing] =
    Server.start(8999, httpApp(queue))

  private val chunkOfProcessIds: Chunk[ProcessId] =
    Chunk.fill(2000)(ProcessId(UUID.randomUUID().toString))

  private def httpApp(queue: Queue[ProcessId]): Http[Any, Nothing, Request, Response] =
    Http.collectZIO {
      case Method.GET -> _ / "text" =>
        for {
          processIdRef <- Ref.make(chunkOfProcessIds)
          id           <- processIdRef.get
          _            <- queue.offerAll(id)
          response     <- ZIO.succeed(Response.text(id.length.toString))
        } yield response
    }

  /*
  def myApp(queue: Queue[Int]): IO[IOException, Unit] =
    for {
      producer <- ZStream
                    .iterate(1)(_ + 1)
                    // .schedule(Schedule.fixed(200.millis))
                    .run(ZSink.fromQueue(queue))
                    .fork
      _        <- aConsumer(queue).fork
//      consumer <- queue.take.flatMap(printLine(_)).forever
      _        <- producer.join
    } yield ()
   */

  private val counter = Ref.make(1)

  private def consumer(queue: Queue[ProcessId]): ZIO[Any, IOException, Unit] =
    ZStream
      .fromQueue(queue)
      .mapZIOParUnordered(100) { z =>
        for {
          c            <- counter
          counterValue <- c.getAndUpdate(_ + 1)
          _            <- ZIO.sleep(2000.millis)
          _            <- printLine(s"Counter -> $counterValue - value ${z.value}")
        } yield ()
      }
      .runDrain

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      queue <- Queue.bounded[ProcessId](4)
      sFork <- server(queue).fork
      cFork <- consumer(queue).fork
      _     <- sFork.zip(cFork).join
    } yield ()

}
