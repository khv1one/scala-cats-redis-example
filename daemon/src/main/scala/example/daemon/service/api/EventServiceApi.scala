package example.daemon.service.api

import cats.effect.Async
import endpoints4s.http4s.client.JsonEntitiesFromCodecs
import endpoints4s.http4s.client.Endpoints
import example.daemon.config.ServiceConfig
import example.rpc.EventApiAlg
import org.http4s.client.Client

trait EventServiceApi[F[_]] {
  def setEventResult(id: String, status: String): F[Unit]
}

object EventServiceApi {
  class Rpc[F[_] : Async](
    client: Client[F],
    conf: ServiceConfig
  ) extends Endpoints(conf.uri, client)
    with EventApiAlg
    with EventServiceApi[F]
    with JsonEntitiesFromCodecs {

    def setEventResult(id: String, status: String): F[Unit] = resultEventApi(id, status)
  }
}