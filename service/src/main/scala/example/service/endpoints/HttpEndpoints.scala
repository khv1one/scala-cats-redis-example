package example.service.endpoints

import cats.effect.kernel.Async
import cats.implicits.catsSyntaxApplicativeId
import endpoints4s.http4s.server.{Endpoints, JsonEntitiesFromCodecs}
import example.rpc.ExampleApiAlg
import org.http4s.{HttpApp, HttpRoutes}

class HttpEndpoints[F[_] : Async]() extends Endpoints[F] with ExampleApiAlg with JsonEntitiesFromCodecs {

  private def firstRout: HttpRoutes[F] = HttpRoutes.of {
    exampleApi.implementedByEffect { param =>
      ().pure[F]
    }
  }

  val allRoutes: HttpApp[F] = {
    firstRout.orNotFound
  }
}
