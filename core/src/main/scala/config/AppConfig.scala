package config

import cats.MonadThrow
import cats.implicits._
import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class AppConfig private[config] (
    db: DbConfig
)

object AppConfig {
  def load[F[_]: MonadThrow](): F[AppConfig] =
    ConfigSource.default.load[AppConfig] match {
      case Left(value) =>
        MonadThrow[F].raiseError(new RuntimeException(value.toString()))
      case Right(value) => value.pure[F]
    }
}
