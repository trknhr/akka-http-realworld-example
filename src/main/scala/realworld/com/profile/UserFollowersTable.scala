package realworld.com.profile

import java.sql.Timestamp
import java.util.Date

import slick.jdbc.PostgresProfile.api.{
  DBIO => _,
  MappedTo => _,
  Rep => _,
  TableQuery => _,
  _
}
import slick.lifted.TableQuery

trait UserFollowersTable {

  def currentWhenInserting = new Timestamp((new Date).getTime)

  class Followers(tag: Tag) extends Table[UserFollower](tag, "followers") {

    def userId = column[Long]("user_id")
    def followeeId = column[Long]("followee_id")
    def insertedAt =
      column[Timestamp]("inserted_at", O.Default(currentWhenInserting))

    def * =
      (userId, followeeId, insertedAt) <> ((UserFollower.apply _).tupled, UserFollower.unapply)
  }

  protected val followers = TableQuery[Followers]
}
