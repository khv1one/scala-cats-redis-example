package example.service.config

import pureconfig.generic.semiauto.deriveReader
import pureconfig.{ConfigReader, ConfigSource}

case class Config(redis: RedisConfig, server: ServerConfig)

object Config {
  implicit val codec: ConfigReader[Config] = deriveReader[Config]
  val getConfig: Config = ConfigSource.default.loadOrThrow[Config]
}

case class RedisConfig(
  hosts: Seq[String],
  dataBus: String
)

object RedisConfig {
  implicit val redis: ConfigReader[RedisConfig] = deriveReader[RedisConfig]
}

case class ServerConfig(
  host: String,
  port: Int
)

object ServerConfig {
  implicit val app: ConfigReader[ServerConfig] = deriveReader[ServerConfig]
}