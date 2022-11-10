package time

import cats.effect.kernel.Sync
import cats.implicits._

import java.time.OffsetDateTime

trait Time[F[_]] {
  def now: F[OffsetDateTime]
  def tomorrow: F[OffsetDateTime]
  def isExpired(other: OffsetDateTime): F[Boolean]
}

object Time {
  def apply[F[_]](implicit t: Time[F]): Time[F] = t
  def instance[F[_]: Sync](): Time[F] = new Time[F] {
    override def now: F[OffsetDateTime] = Sync[F].delay(OffsetDateTime.now())

    override def tomorrow: F[OffsetDateTime] =
      now.map(_.plusDays(1))

    override def isExpired(other: OffsetDateTime): F[Boolean] = for {
      nowRes <- now
      result = nowRes.compareTo(other)
    } yield if (result >= 0) true else false
  }
}
