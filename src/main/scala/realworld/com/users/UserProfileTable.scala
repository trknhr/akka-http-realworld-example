package realworld.com.users

import java.sql.Timestamp
import java.util.Date

import realworld.com.core.User
import realworld.com.utils.DatabaseConnector

trait UserProfileTable {
  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def currentWhenInserting = new Timestamp((new Date).getTime)

    def id = column[Long]("id",  O.AutoInc, O.PrimaryKey)
    def username = column[String]("username")
    def password = column[String]("password")
    def email = column[String]("email")
    def bio = column[Option[String]]("bio")
    def image = column[Option[String]]("image")

    def createdAt = column[Timestamp]("created_at", O.Default(currentWhenInserting))

    def updatedAt = column[Timestamp]("updated_at", O.Default(currentWhenInserting))

    def * = (id, username, password, email, bio,  image, createdAt, updatedAt) <> ((User.apply _).tupled, User.unapply)
  }

  protected val users = TableQuery[Users]
}
