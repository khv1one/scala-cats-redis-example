package example.utils

import cats.{Applicative, Monoid}

object Helpers {
  def applyOrEmptyF[F[_] : Applicative, A: Monoid](cond: Boolean)(f: => F[A]): F[A] = {
    if (cond) f else Applicative[F].pure(Monoid[A].empty)
  }
}
