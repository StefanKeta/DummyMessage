package domain
import MessageTypes._

case class Message (
    id:MessageId,
    from: UserId,
    to:UserId,
    message: MessageText)
