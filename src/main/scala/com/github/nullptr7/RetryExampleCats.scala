package com.github.nullptr7

import cats.effect.{ IO, IOApp, Sync }
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import retry.RetryDetails._
import retry._

object RetryExampleCats extends IOApp.Simple {
  implicit def logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  private def logError(err: Throwable, details: RetryDetails): IO[Unit] = details match {

    case WillDelayAndRetry(_, retriesSoFar: Int, _) =>
      Logger[IO].info(s"Failed to download. So far we have retried $retriesSoFar times.")

    case GivingUp(totalRetries: Int, _) =>
      Logger[IO].error(err)(s"Giving up after $totalRetries retries")

  }

  private val ioErrorLogic =
    Logger[IO].info("Performing a business logic op leading to failure") *> IO.raiseError(new RuntimeException("Error from business layer"))

  private val unreachableCodeDueToPrevError =
    Logger[IO].info("This piece of information will never be printed.")

  private val composedOp: IO[Unit] =
    ioErrorLogic *> unreachableCodeDueToPrevError

  private val flakyOp: IO[Unit] =
    retryingOnAllErrors(RetryPolicies.limitRetries[IO](5), logError)(composedOp)

  override def run: IO[Unit] = flakyOp

}
