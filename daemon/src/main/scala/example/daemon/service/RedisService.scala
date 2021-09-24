package example.daemon.service

import cats.effect.{LiftIO, Sync}
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxApply, catsSyntaxFlatMapOps, catsSyntaxOptionId, toFlatMapOps, toFunctorOps, toTraverseFilterOps}
import example.daemon.config.Config
import example.utils.Helpers.applyOrEmptyF
import example.utils.Syntax.IOExtended
import example.models.{RedisCodec, RedisEvent}
import example.redis.RedisClient
import org.redisson.api.StreamMessageId
import org.typelevel.log4cats.Logger

import scala.util.Try

class RedisService[F[_] : Sync : LiftIO](
  client: RedisClient
)(
  implicit L: Logger[F],
  config: Config
) {

  import config.{redis => redisConfig}

  def initGroup: F[Unit] = {
    client.xGroupCreate(redisConfig.dataBus, redisConfig.group).liftTo[F]
  }

  def readStreamEvents[T: RedisCodec](consumer: String): F[List[RedisEvent[T]]] = {
    client
      .xReadGroup(redisConfig.dataBus, redisConfig.group, consumer, redisConfig.batchSize)
      .map(_.map {case (id, keys) => id -> RedisCodec[T].fromRedisKeys(keys) })
      .liftTo[F]
      .flatMap(entries => parseEvents[T](entries))
  }

  def ackTask(id: StreamMessageId): F[Unit] =
    client.xAck(redisConfig.dataBus, redisConfig.group, Set(id)).void.liftTo[F]

  def ackTasks(ids: Set[StreamMessageId]): F[Unit] =
    client.xAck(redisConfig.dataBus, redisConfig.group, ids).void.liftTo[F]

  def readFailedEvents[T: RedisCodec](consumer: String): F[List[RedisEvent[T]]] = {
    client.xPending(redisConfig.dataBus, redisConfig.group, StreamMessageId.MIN, StreamMessageId.MAX, redisConfig.batchSize)
      .liftTo[F]
      .flatMap { entries =>
        val (recoveringTasks, outdatedTasks) = entries.partition(_.getLastTimeDelivered <= redisConfig.recoverCount)

        applyOrEmptyF(outdatedTasks.nonEmpty) {
          val outdatedTaskIds = outdatedTasks.map(_.getId).toSet

          ackTasks(outdatedTaskIds) >>
            L.error(s"Tasks ${outdatedTaskIds.mkString(",")} will not be processed, max retry attempts was reached")
        }
          .*>(claimFailedEvents(consumer, recoveringTasks.map(_.getId).toSet))
      }
      .map(_.map {case (id, keys) => id -> RedisCodec[T].fromRedisKeys(keys) })
      .flatMap(entries => parseEvents[T](entries))
  }

  private def claimFailedEvents(
    consumer: String,
    ids: Set[StreamMessageId]
  ): F[Map[StreamMessageId, Map[String, String]]] = {
    applyOrEmptyF(ids.nonEmpty) {
      client.xClaim(redisConfig.dataBus, redisConfig.group, consumer, redisConfig.claimIdleTime.toSeconds, ids).liftTo[F]
    }
  }

  private def parseEvents[R: RedisCodec](taskMessages: Map[StreamMessageId, Try[R]]): F[List[RedisEvent[R]]] = {
    taskMessages.toList.traverseFilter { case (id, eventTry) =>
      Sync[F]
        .fromTry(eventTry).map(event => RedisEvent(id, event).some)
        .handleErrorWith(error => ackTask(id) *> L.error(s"${error.getLocalizedMessage}").map(_ => Option.empty))
    }
  }
}
