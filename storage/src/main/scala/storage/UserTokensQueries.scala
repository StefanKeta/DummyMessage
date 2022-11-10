package storage

import dao.UserTokenDao
import doobie.postgres.implicits._
import doobie.implicits._
import doobie._

import java.time.OffsetDateTime
import java.util.UUID

trait UserTokensQueries extends GenericQueries[UserTokenDao] {
  def findToken(token: String): ConnectionIO[Option[UserTokenDao]]
  def remove(token: String): ConnectionIO[Int]
}

object UserTokensQueries {
  def apply(): UserTokensQueries = new UserTokensQueries {
    override def findById(id: UUID): doobie.ConnectionIO[Option[UserTokenDao]] =
      sql"""
           | SELECT * FROM user_tokens WHERE user_id = $id AND expires_at < ${OffsetDateTime
        .now()}
           |""".stripMargin
        .query[UserTokenDao]
        .option

    override def insert(t: UserTokenDao): doobie.ConnectionIO[Int] =
      sql"""
           INSERT INTO user_tokens VALUES (
           ${t.token},
           ${t.userId},
           ${t.expiresAt}
           )
         """.stripMargin.update.run

    override def findToken(
        token: String
    ): doobie.ConnectionIO[Option[UserTokenDao]] =
      sql"""
           SELECT * FROM user_tokens WHERE token = $token
         """.stripMargin
        .query[UserTokenDao]
        .option

    override def remove(token: String): doobie.ConnectionIO[Int] =
      sql"""
           DELETE FROM user_tokens WHERE token = $token
         """.update.run
  }
}
