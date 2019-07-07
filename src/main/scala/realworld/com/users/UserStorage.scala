package realworld.com.users

import realworld.com.core.User
import realworld.com.profile.{ UserFollower, UserFollowersTable }
import realworld.com.utils.DatabaseConnector
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api.{ DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _ }

import scala.concurrent.ExecutionContext

trait UserStorage {
  def getUsers(): DBIO[Seq[User]]
  def getUsersByUserIds(userId: Seq[Long]): DBIO[Seq[User]]
  def getUser(userId: Long): DBIO[Option[User]]
  def getFollowees(userId: Long): DBIO[Seq[User]]
  def getUserByUsername(username: String): DBIO[Option[User]]
  def register(userRegistration: User): DBIO[User]
  def findUserByEmail(email: String, password: String): DBIO[Option[User]]
  def saveUser(user: User): DBIO[User]
  def follow(userId: Long, targetUserId: Long): DBIO[Int]
  def unfollow(userId: Long, targetUserId: Long): DBIO[Int]
  def isFollowing(userId: Long, targetUserId: Long): DBIO[Boolean]
  def followingUsers(userId: Long, targetUserId: Seq[Long]): DBIO[Seq[Long]]
}

class JdbcUserStorage(
  val databaseConnector: DatabaseConnector
)(implicit executionContext: ExecutionContext)
    extends UserProfileTable
    with UserStorage
    with UserFollowersTable {

  def getUsers(): DBIO[Seq[User]] = users.result

  def getUsersByUserIds(userIds: Seq[Long]): DBIO[Seq[User]] =
    users.filter(_.id inSet userIds).result

  def getUserByUsername(username: String): DBIO[Option[User]] =
    users.filter(_.username === username).result.headOption

  def getUser(userId: Long): DBIO[Option[User]] =
    users.filter(_.id === userId).result.headOption

  def getFollowees(userId: Long): DBIO[Seq[User]] =
    followers
      .join(users)
      .on(_.followeeId === _.id)
      .filter(a => a._1.userId === userId)
      .map(_._2)
      .result

  def register(user: User): DBIO[User] =
    (users returning users.map(_.id) into ((u, id) => u.copy(id = id + 1))) += user

  def saveUser(user: User): DBIO[User] = {
    (users returning users).insertOrUpdate(user).map(_.getOrElse(user))
  }

  def findUserByEmail(email: String, password: String): DBIO[Option[User]] =
    users.filter(a => a.email === email && a.password === password).result.headOption

  def follow(userId: Long, targetUserId: Long): DBIO[Int] =
    followers += UserFollower(userId, targetUserId, currentWhenInserting)

  def unfollow(userId: Long, targetUserId: Long): DBIO[Int] = {
    followers.filter(a =>
      a.userId === userId && a.followeeId === targetUserId).delete
  }

  def isFollowing(userId: Long, targetUserId: Long): DBIO[Boolean] =
    followers
      .filter(m => m.userId === userId && m.followeeId === targetUserId)
      .result
      .headOption
      .map(
        _.isDefined
      )

  def followingUsers(userId: Long, targetUserIds: Seq[Long]): DBIO[Seq[Long]] =
    followers
      .filter(m => m.userId === userId)
      .filter(m => m.followeeId inSet targetUserIds)
      .map(
        _.followeeId
      )
      .result
}
