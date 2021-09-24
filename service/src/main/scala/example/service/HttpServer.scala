package example.service

import cats.effect.Resource
import cats.effect.kernel.Async
import example.service.config.ServerConfig
import example.service.endpoints.HttpEndpoints
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Server

import scala.concurrent.ExecutionContext

object HttpServer {
  def blazeServer[F[_]: Async](
    routes: HttpEndpoints[F],
    config: ServerConfig
  )(implicit ec: ExecutionContext): Resource[F, Server] = {
    BlazeServerBuilder[F](ec)
      .bindHttp(config.port, config.host)
      .withHttpApp(routes.endpoints.orNotFound)
      .resource
  }
}