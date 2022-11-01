package domain.user

import enumeratum.EnumEntry.Uppercase
import enumeratum._
import sttp.tapir.Schema

sealed trait Gender extends EnumEntry with Uppercase

object Gender
    extends Enum[Gender]
    with DoobieEnum[Gender]
    with CirceEnum[Gender] {
  case object MALE extends Gender
  case object FEMALE extends Gender

  val values: IndexedSeq[Gender] = findValues

  implicit val schema: Schema[Gender] = Schema.derived[Gender]
}
