package example.utils

import cats.effect.{IO, LiftIO}

import scala.concurrent.Future

object Syntax {

  implicit class FutureExtended[A](future: Future[A]) {
    def toIO(): IO[A] = IO.fromFuture(IO(future))

    def liftTo[F[_] : LiftIO]: F[A] = toIO().liftTo[F]
  }

  implicit class IOExtended[A](io: IO[A]) {
    def liftTo[F[_] : LiftIO]: F[A] = LiftIO[F].liftIO(io)
  }
}
