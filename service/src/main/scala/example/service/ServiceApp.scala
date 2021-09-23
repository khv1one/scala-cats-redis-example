package example.service

import cats.effect.{ExitCode, IO, IOApp}
import example.service.config.Config
import example.service.endpoints.HttpEndpoints
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.ExecutionContext


object ServiceApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    implicit val ec: ExecutionContext = ExecutionContext.global
    implicit val config: Config = Config.getConfig
    implicit def unsafeLogger[F[_]]: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    HttpServer.blazeServer(new HttpEndpoints[IO], config.server)
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}
