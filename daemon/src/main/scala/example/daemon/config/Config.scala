package example.daemon.config

import org.http4s.Uri
import pureconfig.ConfigConvert.catchReadError
import pureconfig.generic.semiauto.deriveReader
import pureconfig.{ConfigReader, ConfigSource}

import scala.concurrent.duration.FiniteDuration

case class Config(redis: RedisConfig, app: AppConfig, service: ServiceConfig)

object Config {
  implicit val codec: ConfigReader[Config] = deriveReader[Config]
  val getConfig: Config = ConfigSource.default.loadOrThrow[Config]
}

case class RedisConfig(
  hosts: Seq[String],
  recoverCount: Int,
  claimIdleTime: FiniteDuration,
  batchSize: Int,
  dataBus: String,
  group: String
)

object RedisConfig {
  implicit val redis: ConfigReader[RedisConfig] = deriveReader[RedisConfig]
}

case class AppConfig(
  concurrent: Int,
  readInterval: FiniteDuration,
  readRecoverInterval: FiniteDuration,
)

object AppConfig {
  implicit val app: ConfigReader[AppConfig] = deriveReader[AppConfig]
}

case class ServiceConfig(uri: Uri)

object ServiceConfig {
  implicit val service: ConfigReader[ServiceConfig] = deriveReader[ServiceConfig]
  implicit val uri: ConfigReader[Uri] = ConfigReader.fromString[Uri](catchReadError(Uri.unsafeFromString))
}
