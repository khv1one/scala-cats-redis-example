package example.daemon

import cats.effect.{ExitCode, IO, IOApp}
import example.daemon.config.Config
import example.daemon.service.api.EventServiceApi
import example.daemon.service.{LogicServiceAlg, RedisService}
import example.daemon.worker.EventWorker
import example.redis.RedisClusterClient
import org.http4s.blaze.client.BlazeClientBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}

import scala.concurrent.ExecutionContext

object DaemonApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val ec: ExecutionContext = ExecutionContext.global

    implicit val config: Config = Config.getConfig
    implicit def unsafeLogger[F]: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    BlazeClientBuilder[IO](ec).resource.use { client =>
      val worker = new EventWorker[IO](
        new LogicServiceAlg.LogicServiceIO(),
        new RedisService[IO](RedisClusterClient(config.redis.hosts)),
        new EventServiceApi.Rpc[IO](client, config.service)
      )

      Logger[IO].info("App run") >> worker.run.as(ExitCode.Success)
    }
  }
}