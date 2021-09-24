package example.service.services

import cats.effect.{LiftIO, Sync}
import example.models.RedisCodec
import example.redis.RedisClient
import example.service.config.Config
import example.utils.Syntax.IOExtended
import org.redisson.api.StreamMessageId

class RedisService[F[_] : Sync : LiftIO](
  client: RedisClient
)(implicit config: Config) {
  def addEvent[T: RedisCodec](event: T): F[StreamMessageId] = {
    client.xAdd(config.redis.dataBus, RedisCodec[T].toRedisKeys(event)).liftTo[F]
  }
}