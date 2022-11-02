package error

sealed trait ServerError

case class FailedToHashPassword(message: String)
    extends Throwable(s"Failed to hash password: $message")
    with ServerError
