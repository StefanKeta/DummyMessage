package storage

import dao.UserTokenDao
import doobie.postgres.implicits._
import doobie.implicits._
import doobie._

import java.time.OffsetDateTime
import java.util.UUID

trait UserTokensQueries extends GenericQueries[UserTokenDao]

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
  }
}
