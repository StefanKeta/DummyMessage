package error

sealed trait ServerError

case class FailedToHashPassword()
    extends Throwable("Failed to hash password")
    with ServerError
