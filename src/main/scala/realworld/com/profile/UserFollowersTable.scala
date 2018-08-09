package realworld.com.profile

import java.sql.Timestamp
import java.util.Date

import realworld.com.utils.DatabaseConnector

trait UserFollowersTable {
  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class Followers(tag: Tag) extends Table[UserFollower](tag, "user_followers") {
    def currentWhenInserting = new Timestamp((new Date).getTime)

    def userId = column[Long]("user_id")
    def followeeId = column[Long]("followee_id")
    def insertedAt = column[Timestamp]("inserted_at", O.Default(currentWhenInserting))

    def * = (userId, followeeId, insertedAt) <> ((UserFollower.apply _).tupled, UserFollower.unapply)
  }

  protected val followers = TableQuery[Followers]
}
