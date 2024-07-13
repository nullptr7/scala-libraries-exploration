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
//  val updateResponseOnTimeout =
//    RequestHandlerMiddlewares
//      .updateResponse(_ => Response(Status.RequestTimeout, body = Body.fromString("Timed Out")))


  private val middleware = Middleware.timeout(Duration.fromSeconds(3))

  private lazy val patchEnv = Middleware.addHeader("X-Environment", "Dev")


    // .map(_ => Response(Status.RequestTimeout, body = Body.fromString("Timed Out")))

  // val superCombined = composedMiddlewares ++ HttpAppMiddleware.debug

  /*override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server.serve(combined).provide(Server.defaultWithPort(8080))*/
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server.serve(combined).provide(Server.defaultWithPort(8080))

//  private lazy val combined = app @@ updateResponseOnTimeout
  private lazy val combined = routes @@ middleware @@ patchEnv

  private val zioDuration: Duration = Duration.fromMillis(6000)

  private lazy val routes =
    Routes(
      Method.GET / Root -> handler(Response.text("Greetings at your service")),
      Method.GET / "greet" -> handler { (req: Request) =>
        val name = req.queryParamToOrElse("name", "World")
        Response.text(s"Hello $name!")
      }
    )

//  private val app: Http[Any, Nothing, Request, Response] = ZioHttp.collect[Request] {
//    case Method.GET -> Root / "api" / "greet" / name =>
//      Thread.sleep(2000)
//      Response.text(s"Hello, $name")
//  }

}
