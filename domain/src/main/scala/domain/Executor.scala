package domain

import domain.user.{BasicCredentials, User}

trait Executor[F[_]] {
  def register(user: User): F[Unit]
  def activate(string: String): F[Unit]
  def login(basicCredentials: BasicCredentials):F[String]
}