package domain

import domain.user.User

trait Executor[F[_]] {
  def register(user: User): F[Unit]
}