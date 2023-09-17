package com.github.nullptr7

//import zhttp.http
//import zhttp.http._
//import zhttp.service.Server
import zio._
import zio.http._
import zio.http.Server

import java.io.IOException
import scala.language.postfixOps

object ZIOHttpExample extends ZIOAppDefault {
  val updateResponseOnTimeout =
    RequestHandlerMiddlewares
      .updateResponse(_ => Response(Status.RequestTimeout, body = Body.fromString("Timed Out")))


    // .map(_ => Response(Status.RequestTimeout, body = Body.fromString("Timed Out")))

  // val superCombined = composedMiddlewares ++ HttpAppMiddleware.debug

  /*override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server.serve(combined).provide(Server.defaultWithPort(8080))*/
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server.serve(combined).provide(Server.defaultWithPort(8080))

  private lazy val combined = app @@ updateResponseOnTimeout

  private val zioDuration: Duration = Duration.fromMillis(6000)

  private val app: Http[Any, Nothing, Request, Response] = Http.collect[Request] {
    case Method.GET -> Root / "api" / "greet" / name =>
      Thread.sleep(2000)
      Response.text(s"Hello, $name")
  }

}
