package users

import java.sql.Timestamp
import java.util.Date

case class User(id: Long, username: String, password: String, email: String, bio: Option[String], image: Option[String], createdAt: Timestamp, updatedAt: Timestamp) {
  require(username.nonEmpty, "username.empty")
  require(password.nonEmpty, "password.empty")
  require(email.nonEmpty, "email.empty")
}

case class UserRegistration(username: String, password: String, email: String) {
  def create(): User ={
    User(0, username, password, email, None, None,  new Timestamp((new Date).getTime),  new Timestamp((new Date).getTime))
  }
}

