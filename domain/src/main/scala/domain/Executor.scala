package domain

import user.User

trait Executor {
  def register(user:User)
}
