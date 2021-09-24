package example.daemon.worker

import cats.effect.Temporal
import cats.effect.std.Console
import cats.implicits._
import example.daemon.config.Config
import example.daemon.errors.CustomErrors
import example.daemon.service.api.EventServiceApi
import example.daemon.service.{LogicServiceAlg, RedisService}
import example.models.{ExampleEventBody, RedisEvent}
import fs2.Stream
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.DurationInt

class EventWorker[F[_] : Temporal : Console](
  logicService: LogicServiceAlg[F],
  redisService: RedisService[F],
  eventService: EventServiceApi[F]
)(implicit L: Logger[F], config: Config) {

  private val name = getClass.getSimpleName

  def run: F[Unit] = (init ++ Stream.retry(process.compile.drain, 1.seconds, identity, maxAttempts = 1)).compile.drain

  private val init: Stream[F, Unit] =
    Stream
      .eval(redisService.initGroup.handleError(error => L.warn(s"${error.getLocalizedMessage}")))

  private val readTask =
    Stream
      .awakeDelay[F](config.app.readInterval)
      .evalMap(_ => redisService.readStreamEvents[ExampleEventBody](name))
      .filter(_.nonEmpty)
      .evalTap(entries => L.info(s"Got ${entries.size} new events"))

  private val recover =
    Stream
      .awakeDelay[F](config.app.readRecoverInterval)
      .evalMap(_ => redisService.readFailedEvents[ExampleEventBody](name))
      .filter(_.nonEmpty)
      .evalTap(entries => L.info(s"Got ${entries.size} recovered events"))

  private val process: Stream[F, Unit] =
    readTask
      .merge(recover)
      .flatMap(entries =>
        Stream
          .emits(entries)
          .covary[F]
          .parEvalMapUnordered(config.app.concurrent)(event =>
            logicService.run(event.body)
              .>>(redisService.ackTask(event.id))
              .>>(L.info(s"End Tasks ${event.idString} Successful"))
              .>>(eventService.setEventResult(event.idString, "Successful"))
              .handleErrorWith(error => handleError(error, event))
          )
      )

  private def handleError[T](error: Throwable, event: RedisEvent[T]): F[Unit] = {
    (error match {
      case error: CustomErrors =>
        if (error.isRecoverable) {
          L.error(s"End Tasks ${event.idString} with custom ${error.getLocalizedMessage}")
        } else {
          L.warn(s"End Tasks ${event.idString} with custom ${error.getLocalizedMessage}, but task ack")
            .>>(redisService.ackTask(event.id))
        }
      case error => L.error(s"End Tasks ${event.idString} with error ${error.getLocalizedMessage}")
    })
      .>>(eventService.setEventResult(event.idString, "Error"))
  }
}