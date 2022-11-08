package dao

import java.time.OffsetDateTime
import java.util.UUID

case class UserTokenDao(
    token: String,
    userId: UUID,
    expiresAt: OffsetDateTime
)
