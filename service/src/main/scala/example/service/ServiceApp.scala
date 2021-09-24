package example.service

import cats.effect.{ExitCode, IO, IOApp}
import example.redis.RedisClusterClient
import example.service.config.Config
import example.service.endpoints.HttpEndpoints
import example.service.services.RedisService
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.ExecutionContext


object ServiceApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    implicit val ec: ExecutionContext = ExecutionContext.global
    implicit val config: Config = Config.getConfig
    implicit def unsafeLogger[F]: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    val endpoints = new HttpEndpoints[IO](new RedisService[IO](RedisClusterClient(config.redis.hosts)))

    HttpServer.blazeServer(endpoints, config.server)
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}
