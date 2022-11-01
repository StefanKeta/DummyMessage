package storage

import dao.UserDao
import domain.user.Gender
import doobie.Meta
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import enumeratum.Enum

import java.util.UUID

trait UserQueries extends GenericQueries[UserDao] {
  def findByEmail(email: String): ConnectionIO[Option[UserDao]]
}

object UserQueries {

  implicit val genderPgEnum: Meta[Gender] =
    pgEnumStringOpt[Gender](
      "gender",
      implicitly[Enum[Gender]].withNameOption,
      _.entryName
    )

  def apply(): UserQueries =
    new UserQueries {
      override def findById(id: UUID): ConnectionIO[Option[UserDao]] =
        sql"""
            SELECT * FROM user_account WHERE user_id = $id
         """.query[UserDao].option

      override def insert(userDao: UserDao): ConnectionIO[Int] =
        sql"""
                     INSERT INTO user_account VALUES
                     (
                     ${userDao.uuid},
                     ${userDao.firstName},
                     ${userDao.lastName},
                     ${userDao.gender},
                     ${userDao.dob},
                     ${userDao.email},
                     ${userDao.password},
                     ${userDao.activated},
                     ${userDao.createdAt}
                     )
                   """.update.run

      override def findByEmail(email: String): ConnectionIO[Option[UserDao]] =
        sql"""
            SELECT * FROM user_account WHERE email = $email
           """.query[UserDao].option
    }
}
