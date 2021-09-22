package example.redis

import cats.effect.IO
import org.redisson.api._
import org.redisson.api.stream.{StreamAddArgs, StreamReadGroupArgs}
import org.redisson.client.codec.StringCodec

import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters.{IterableHasAsScala, ListHasAsScala, MapHasAsJava, MapHasAsScala}

trait RedisOperationsIO {
  protected val client: RedissonClient

  private lazy val keys = client.getKeys
  private lazy val set: String => RSet[String] = client.getSet[String](_, StringCodec.INSTANCE)
  private lazy val stream: String => RStream[String, String] = client.getStream[String, String](_, StringCodec.INSTANCE)

  def xGroupCreate(key: String, group: String): IO[Unit] = IO {
    stream(key).createGroup(group)
  }

  def xAdd(key: String, kv: Map[String, String]): IO[StreamMessageId] = IO {
    stream(key).add(StreamAddArgs.entries(kv.asJava))
  }

  def xDel(key: String, id: StreamMessageId): IO[Long] = IO {
    stream(key).remove(id)
  }

  def xAck(key: String, group: String, ids: Set[StreamMessageId]): IO[Long] = IO {
    stream(key).ack(group, ids.toSeq: _*)
  }

  def xPending(
    key: String,
    group: String,
    startId: StreamMessageId,
    endId: StreamMessageId,
    count: Int
  ): IO[List[PendingEntry]] = IO {
    stream(key).listPending(group, startId, endId, count).asScala.toList
  }

  def xClaim(
    key: String,
    group: String,
    consumer: String,
    idleSeconds: Long,
    ids: Set[StreamMessageId]
  ): IO[Map[StreamMessageId, Map[String, String]]] = IO {
    stream(key)
      .claim(group, consumer, idleSeconds, TimeUnit.SECONDS, ids.toSeq: _*)
      .asScala
      .map { case (key, values) => (key, values.asScala.toMap) }
      .toMap
  }

  def xReadGroup(
    key: String,
    group: String,
    consumer: String,
    count: Int
  ): IO[Map[StreamMessageId, Map[String, String]]] = IO {
    val args = StreamReadGroupArgs.neverDelivered().count(count)

    stream(key).readGroup(group, consumer, args).asScala.toMap.map { case (key, values) => (key, values.asScala.toMap) }
  }

  def scan(pattern: String): IO[Iterable[String]] = IO {
    keys.getKeysByPattern(pattern).asScala
  }

  def del(keySet: Set[String]): IO[Long] = IO {
    keys.delete(keySet.toList: _*)
  }

  def sAdd(key: String, value: String): IO[Boolean] = IO {
    set(key).add(value)
  }

  def sMembers(key: String): IO[Set[String]] = IO {
    set(key).readAll().asScala.toSet
  }
}
