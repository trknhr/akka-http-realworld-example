package realworld.com

import java.sql.Timestamp

package object profile {
  case class Profile(
    username: String,
    bio: Option[String],
    image: Option[String],
    following: Boolean)

  case class ResponseProfile(profile: Profile)
  case class UserFollower(userId: Long, followeeId: Long, insertedAt: Timestamp)
}
