package com.github.nullptr7

import sttp.model.StatusCode
import sttp.monad.MonadError
import sttp.tapir.{AttributeKey, PublicEndpoint}
import sttp.tapir.generic.auto._
import sttp.tapir.json.zio.jsonBody
import sttp.tapir.server.interceptor.{DecodeFailureContext, DecodeSuccessContext, EndpointHandler, EndpointInterceptor, Responder, SecurityFailureContext}
import sttp.tapir.server.interpreter.BodyListener
import sttp.tapir.server.model.{ServerResponse, ValuedEndpointOutput}
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import zio.http.{HttpApp, Response, Routes, Server}
import zio.json._
import zio.{&, Duration, ExitCode, Task, URIO, ZIO, ZIOAppDefault, ZLayer}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

object TapirZIOExample extends ZIOAppDefault {
  case class Pet(species: String, url: String)

  implicit val petCodec: JsonCodec[Pet] = DeriveJsonCodec.gen[Pet]

  private val petEndpoint: PublicEndpoint[Int, String, Pet, Any] =
    endpoint
      .get
      .in("pet" / path[Int]("petId"))
      .attribute(AttributeKey[ServerTimeout], ServerTimeout(FiniteDuration(1, TimeUnit.SECONDS)))
      .errorOut(stringBody)
      .out(jsonBody[Pet])

  val petRoutes: Routes[Any & Any, Response] =
    ZioHttpInterpreter(ZioHttpServerOptions.customiseInterceptors.options.prependInterceptor(Interceptor.apply())).toHttp(
      petEndpoint.zServerLogic(petId =>
        if (petId == 35)
          ZIO.succeed(Pet("Tapirus terrestris", "https://en.wikipedia.org/wiki/Tapir")).delay(Duration.fromSeconds(5))
        else ZIO.fail("Unknown pet id")
      )
    )

  // Same as above, but combining endpoint description with server logic:
  private val petServerEndpoint: ZServerEndpoint[Any, Any] = petEndpoint.zServerLogic { petId =>
    if (petId == 35)
      ZIO.succeed(Pet("Tapirus terrestris", "https://en.wikipedia.org/wiki/Tapir")).delay(Duration.fromSeconds(5))
    else
      ZIO.fail("Unknown pet id")
  }

  // Docs
  private val swaggerEndpoints: List[ZServerEndpoint[Any, Any]] =
    SwaggerInterpreter().fromEndpoints[Task](List(petEndpoint), "Our pets", "1.0")

  // Starting the server
  private val routes: Routes[Any & Any, Response] =
    ZioHttpInterpreter(ZioHttpServerOptions.customiseInterceptors.options.prependInterceptor(Interceptor.apply())).toHttp(List(petServerEndpoint) ++ swaggerEndpoints)

  override def run: URIO[Any, ExitCode] =
    ZIO.log("Running Server") *> Server
      .serve(routes)
      .provide(
        ZLayer.succeed(Server.Config.default.port(8080)),
        Server.live,
      )
      .exitCode

  case class ServerTimeout(duration: FiniteDuration)

  private val serverTimeout: AttributeKey[ServerTimeout] = AttributeKey[ServerTimeout]

  def apply(duration: FiniteDuration) = new ServerTimeout(duration)

  private object Interceptor {
    def apply(): EndpointInterceptor[Task] =
      new EndpointInterceptor[Task] {
        override def apply[B](responder: Responder[Task, B], endpointHandler: EndpointHandler[Task, B]): EndpointHandler[Task, B] =
          new EndpointHandler[Task, B] {
            override def onDecodeSuccess[A, U, I](ctx: DecodeSuccessContext[Task, A, U, I])(implicit monad: MonadError[Task], bodyListener: BodyListener[Task, B]): Task[ServerResponse[B]] =
              ctx.endpoint.attribute(serverTimeout) match {
                case Some(ServerTimeout(duration)) =>
                  endpointHandler
                    .onDecodeSuccess(ctx)
                    .timeout(Duration.fromSeconds(10L))
                    .flatMap {
                      case Some(value) => ZIO.succeed(value)
                      case None        =>
                        val vEOpt =
                          ValuedEndpointOutput[String](
                            jsonBody[String].and(statusCode(StatusCode.RequestTimeout)),
                            "There was a request timeout",
                          )
                        responder(ctx.request, vEOpt)
                    }
                case None                          => endpointHandler.onDecodeSuccess(ctx)
              }

            override def onSecurityFailure[A](ctx: SecurityFailureContext[Task, A])(implicit monad: MonadError[Task], bodyListener: BodyListener[Task, B]): Task[ServerResponse[B]] = endpointHandler.onSecurityFailure(ctx)

            override def onDecodeFailure(ctx: DecodeFailureContext)(implicit monad: MonadError[Task], bodyListener: BodyListener[Task, B]): Task[Option[ServerResponse[B]]] = endpointHandler.onDecodeFailure(ctx)

          }

      }

  }

}
