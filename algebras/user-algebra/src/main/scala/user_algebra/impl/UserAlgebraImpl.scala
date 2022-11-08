package user_algebra.impl

import cats.implicits._
import cats.effect.Sync
import dao.{UserDao, UserTokenDao}
import domain.user.User
import doobie.implicits._
import doobie.util.transactor.Transactor
import error._
import password.PasswordHasher
import storage.{UserQueries, UserTokensQueries}
import time.Time
import token.SecureRandom
import user_algebra.UserAlgebra

import java.time.OffsetDateTime
import java.util.UUID

private[user_algebra] class UserAlgebraImpl[F[_]](
    userQueries: UserQueries,
    userTokensQueries: UserTokensQueries
)(implicit
    F: Sync[F],
    xa: Transactor[F],
    passwordHasher: PasswordHasher[F],
    secureRandom: SecureRandom[F],
    time: Time[F]
) extends UserAlgebra[F] {
  override def registerUser(user: User): F[String] = for {
//    _ <- validateEmail(user.email)
    _ <- validateFirstName(user.firstName)
    _ <- validateLastName(user.lastName)
    hashedPassword <- passwordHasher.hashPassword(user.password)
    token <- secureRandom.generateToken()
    uuid <- secureRandom.generateUuid()
    tomorrow <- time.tomorrow
    program = for {
      userOpt <- userQueries.findByEmail(user.email)
      _ <- userOpt match {
        case Some(user) =>
          WeakAsyncConnectionIO.raiseError[String](
            UserAlreadyExists(s"User with ${user.email} already exists!")
          )
        case None =>
          for {
            _ <- insertUser(uuid, user, hashedPassword, userQueries)
            tokenDao = UserTokenDao(token, uuid, tomorrow)
            _ <- insertToken(tokenDao, userTokensQueries)
          } yield ()
      }
    } yield token
    transactionResult <- program.transact(xa)
  } yield transactionResult

  private def insertUser(
      userId: UUID,
      user: User,
      password: String,
      userQueries: UserQueries
  ) =
    userQueries.insert(
      UserDao(
        uuid = userId,
        firstName = user.firstName,
        lastName = user.lastName,
        gender = user.gender,
        dob = user.dob,
        email = user.email,
        password = password,
        activated = false,
        createdAt = OffsetDateTime.now()
      )
    )

  private def insertToken(
      tokenDao: UserTokenDao,
      userTokensQueries: UserTokensQueries
  ) =
    userTokensQueries
      .insert(tokenDao)

  private def validateEmail(email: String): F[Unit] =
    if (
      email.matches(
        """(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])
          |""".stripMargin
      )
    ) F.unit
    else F.raiseError(InvalidEmail(s"$email is not a valid email!"))

  def validateFirstName(firstName: String): F[Unit] =
    if (!firstName.matches("^[a-zA-Z]+$"))
      F.raiseError(InvalidFirstName(s"$firstName is not a valid first name!"))
    else F.unit

  def validateLastName(lastName: String): F[Unit] =
    if (!lastName.matches("^[a-zA-Z]+$"))
      F.raiseError(InvalidLastName(s"$lastName is not a valid last name!"))
    else F.unit

}
