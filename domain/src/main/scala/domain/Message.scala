package domain

import java.util.UUID

case class Message(messageUuid: UUID, from: UUID, to: UUID, message: String)
