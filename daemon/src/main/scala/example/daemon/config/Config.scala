package example.daemon.config

import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.generic.semiauto.deriveReader

import scala.concurrent.duration.FiniteDuration

case class Config(redis: RedisConfig, app: AppConfig)

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