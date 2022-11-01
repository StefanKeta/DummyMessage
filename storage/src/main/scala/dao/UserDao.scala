package dao

import domain.user.Gender

import java.time.{LocalDate, OffsetDateTime}
import java.util.UUID

case class UserDao(
    uuid: UUID,
    firstName: String,
    lastName: String,
    gender: Gender,
    dob: LocalDate,
    email: String,
    password: String,
    activated: Boolean,
    createdAt: OffsetDateTime
)
