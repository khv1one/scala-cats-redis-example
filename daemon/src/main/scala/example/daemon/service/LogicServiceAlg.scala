package example.daemon.service

import cats.effect.IO
import example.models.ExampleEventBody

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait LogicServiceAlg[F[_]] {
  def run(exampleEvent: ExampleEventBody): F[Unit]
}

object LogicServiceAlg {

  class LogicServiceIO extends LogicServiceAlg[IO] {
    def run(exampleEvent: ExampleEventBody): IO[Unit] = {
      IO(println(s"run $exampleEvent"))
    }
  }

  class LogicServiceFuture extends LogicServiceAlg[Future] {
    def run(exampleEvent: ExampleEventBody): Future[Unit] = {
      Future(println(s"run $exampleEvent"))
    }
  }
}
