package token

import cats.effect.kernel.Sync
import cats.effect.std.{Random, UUIDGen}
import cats.implicits._
import fs2.Stream

import java.util.UUID

trait SecureRandom[F[_]] {
  def generateToken(): F[String]
  def generateUuid(): F[UUID]
}

object SecureRandom {
  def apply[F[_]](implicit random: SecureRandom[F]): SecureRandom[F] = random
  def instance[F[_]: Sync](): SecureRandom[F] = new SecureRandom[F] {
    override def generateToken(): F[String] = for {
      random <- Random.scalaUtilRandom[F]
      array = random.nextBytes(64)
      token <- Stream
        .evalSeq(array.map(_.toSeq))
        .through(fs2.text.base64.encode[F])
        .compile
        .string
    } yield token

    override def generateUuid(): F[UUID] = UUIDGen.randomUUID
  }
}
