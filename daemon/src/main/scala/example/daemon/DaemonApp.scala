package example.daemon

import cats.effect.{ExitCode, IO, IOApp}
import example.daemon.config.Config
import example.daemon.service.{LogicServiceAlg, RedisService}
import example.daemon.worker.EventWorker
import example.redis.RedisClusterClient
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}

object DaemonApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    implicit val config: Config = Config.getConfig
    implicit def unsafeLogger[F[_]]: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    val worker = new EventWorker[IO](
      new LogicServiceAlg.LogicServiceIO(),
      new RedisService[IO](RedisClusterClient(config.redis.hosts))
    )

    Logger[IO].info("App run") >> worker.run.as(ExitCode.Success)
  }
}