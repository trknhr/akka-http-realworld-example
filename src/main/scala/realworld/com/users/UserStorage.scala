package realworld.com.users


import realworld.com.core.User
import realworld.com.profile.{UserFollower, UserFollowersTable}
import realworld.com.utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

sealed trait UserStorage {
  def getUsers(): Future[Seq[User]]
  def getUser(userId: Long): Future[Option[User]]
  def getUserByUsername(username: String): Future[Option[User]]
  def register(userRegistration: User): Future[User]
  def findUserByEmail(email: String): Future[Option[User]]
  def saveUser(user: User): Future[User];
  def isFollowing(userId: Long, targetUserId: Long): Future[Boolean]
}

class JdbcUserStorage (
  val databaseConnector: DatabaseConnector
  )(implicit executionContext: ExecutionContext) extends UserProfileTable with UserStorage with UserFollowersTable {
  import databaseConnector._
  import databaseConnector.profile.api._



  def getUsers(): Future[Seq[User]] = db.run(users.result)

  def getUserByUsername(username: String): Future[Option[User]] =
    db.run(
      users.filter(_.username === username).result.headOption
    )

  def getUser(userId: Long): Future[Option[User]] =
    db.run(users.filter(_.id === userId).result.headOption)

  def register(user: User): Future[User] = {
    val userWithId =
      (users returning users.map(_.id) into ((u, id) => u.copy(id = id + 1) ) ) += user

    db.run(userWithId)
  }

  def saveUser(user: User): Future[User] = {
    db.run(users.insertOrUpdate(user)).map(_ => user)
  }

  def findUserByEmail(email: String): Future[Option[User]] =
    db.run(users.filter(_.email === email).result.headOption)


  def follow(userId: Long, targetUserId: Long): Future[Int] =
    db.run(
      followers.insertOrUpdate(UserFollower(userId, targetUserId, null))
    )

  def isFollowing(userId: Long, targetUserId: Long): Future[Boolean] = db.run(
    followers.filter(m => m.userId === userId && m.followeeId === targetUserId).result.headOption.map(
      _.exists(m => false)
    )
  )
}