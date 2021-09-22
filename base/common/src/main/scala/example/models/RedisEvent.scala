package example.models

import org.redisson.api.StreamMessageId

import scala.util.{Failure, Success, Try}

trait RedisCodec[T] {
  def fromRedisKeys: Map[String, String] => Try[T]
  def toRedisKeys: T => Map[String, String]
}

object RedisCodec {
  def apply[T](implicit ev: RedisCodec[T]): RedisCodec[T] = ev
}

case class ExampleEventBody(
  group: String,
  message: String,
  action: String,
)

object ExampleEventBody {
  implicit val redisCodec: RedisCodec[ExampleEventBody] = new RedisCodec[ExampleEventBody] {
    override def fromRedisKeys: Map[String, String] => Try[ExampleEventBody] = { keys =>
      (for {
        group <- keys.get("group")
        message <- keys.get("message")
        action <- keys.get("action")
      } yield ExampleEventBody(group, message, action))
        .map(Success(_))
        .getOrElse(Failure(new NoSuchElementException(s"Can`t parse $keys to ExampleTask, event ack")))
    }

    override def toRedisKeys: ExampleEventBody => Map[String, String] = { event =>
      Map(
        "group" -> event.group,
        "message" -> event.message,
        "action" -> event.action
      )
    }
  }
}

case class RedisEvent[T : RedisCodec](id: StreamMessageId, body: T) {
  val idString: String = id.toString
}