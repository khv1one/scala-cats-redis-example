package example.service.endpoints

import cats.effect.kernel.Async
import cats.implicits.{toFlatMapOps, toSemigroupKOps}
import endpoints4s.http4s.server.{Endpoints, JsonEntitiesFromCodecs}
import example.models.ExampleEventBody
import example.rpc.EventApiAlg
import example.service.services.RedisService
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger

class HttpEndpoints[F[_] : Async](
  redisService: RedisService[F]
)(implicit L: Logger[F]) extends Endpoints[F] with EventApiAlg with JsonEntitiesFromCodecs {

  private def pushEvent: HttpRoutes[F] = HttpRoutes.of {
    pushEventApi.implementedByEffect { case (message, action, group) =>
      redisService.addEvent(ExampleEventBody(message = message, action = action, group = group))
        .flatMap(id => L.info(s"Redis event set, id: ${id.toString}"))
    }
  }

  private def resultEvent: HttpRoutes[F] = HttpRoutes.of {
    resultEventApi.implementedByEffect { case (id, status) =>
      L.info(s"Handle event status changed, id: $id, status: $status")
    }
  }

  def endpoints: HttpRoutes[F] = pushEvent <+> resultEvent
}
