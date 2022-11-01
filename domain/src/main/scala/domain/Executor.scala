package domain

import cats.effect.kernel.Async
import domain.user.User
import error.ErrorInfo
import sttp.model.StatusCode

trait Executor[F[_]] {
  def register(user: User): F[StatusCode]
}