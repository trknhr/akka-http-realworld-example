package realworld.com

import java.sql.Timestamp
import java.util.Date
import com.roundeights.hasher.Implicits._

package object core {
  type AuthToken = String

  final case class AuthTokenContent(userId: Long)
  final case class AuthTokenContent2(userId: String)

  case class User(
    id: Long,
    username: String,
    password: String,
    email: String,
    bio: Option[String],
    image: Option[String],
    createdAt: Timestamp,
    updatedAt: Timestamp) {
    require(username.nonEmpty, "username.empty")
    require(password.nonEmpty, "password.empty")
    require(email.nonEmpty, "email.empty")
  }

  case class ResponseUser(user: UserWithToken)

  case class UserWithToken(
    username: String,
    email: String,
    bio: Option[String],
    image: Option[String],
    token: AuthToken)

  case class UserUpdate(
    username: Option[String],
    password: Option[String],
    email: Option[String],
    bio: Option[String],
    image: Option[String]) {
    def merge(user: User): User = {
      User(
        user.id,
        username.getOrElse(user.username),
        password
          .flatMap(s => Some(s.sha256.hex))
          .getOrElse(user.password.sha256),
        email.getOrElse(user.email),
        bio.orElse(user.bio),
        image.orElse(user.image),
        user.createdAt,
        new Timestamp((new Date).getTime))
    }
  }

  case class UserRegistration(
    username: String,
    password: String,
    email: String) {
    def create(): User = {
      User(
        0,
        username,
        password.sha256.hex,
        email,
        None,
        None,
        new Timestamp((new Date).getTime),
        new Timestamp((new Date).getTime))
    }
  }
}
