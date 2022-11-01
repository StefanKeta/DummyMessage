package storage
import doobie._

import java.util.UUID

trait GenericQueries[T] {
  def findById(id: UUID): ConnectionIO[Option[T]]
  def insert(t: T): ConnectionIO[Int]
}
