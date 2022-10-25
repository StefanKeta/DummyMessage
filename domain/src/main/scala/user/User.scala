package user

import domain.UserTypes._
import io.circe.generic.semiauto.deriveCodec
case class User(
    userId: UserId,
    firstName: FirstName,
    lastName: LastName,
    email: Email,
    password: Password
)

object User {
  implicit val codec = deriveCodec[User]
}
