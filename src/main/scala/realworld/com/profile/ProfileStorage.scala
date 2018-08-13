package realworld.com.profile

import realworld.com.core.User
import realworld.com.users.UserProfileTable
import realworld.com.utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}
//import utils.DatabaseConnector

//trait ProfileStorage {
//  def isFollowing(userId: Long, targetUserId: Long): Future[Boolean]
////  def follow(username: String)
//}
//
//class JdbcProfileStorage(
//    val databaseConnector: DatabaseConnector
//  )(implicit executionContext: ExecutionContext) extends UserProfileTable with UserFollowersTable with ProfileStorage {
//    import databaseConnector._
//    import databaseConnector.profile.api._
//
//  def isFollowing(userId: Long, targetUserId: Long): Future[Boolean] = db.run(
//    followers.filter(m => m.userId === userId && m.followeeId === targetUserId).result.headOption.map(
//      _.exists(m => false)
//    )
//  )
//
////  def follow()
//}

//package users
//
//import utils.DatabaseConnector
//import users.core.User
//
//import scala.concurrent.{ExecutionContext, Future}
//
//sealed trait UserStorage {
//  def getUsers(): Future[Seq[User]]
//  def getUser(userId: Long): Future[Option[User]]
//  def register(userRegistration: User): Future[User]
//  def findUserByEmail(email: String): Future[Option[User]]
//  def saveUser(user: User): Future[User];
//}
//
//class JdbcUserStorage (
//                        val databaseConnector: DatabaseConnector
//                      )(implicit executionContext: ExecutionContext) extends UserProfileTable with UserStorage {
//  import databaseConnector._
//  import databaseConnector.profile.api._
//
//
//
//  def getUsers(): Future[Seq[User]] = db.run(users.result)
//
//  def getUser(userId: Long): Future[Option[User]] =
//    db.run(users.filter(_.id === userId).result.headOption)
//
//  def register(user: User): Future[User] = {
//    val userWithId =
//      (users returning users.map(_.id) into ((u, id) => u.copy(id = id + 1) ) ) += user
//
//    db.run(userWithId)
//  }
//
//  def saveUser(user: User): Future[User] =
//    db.run(users.insertOrUpdate(user)).map(_ => user)
//
//  def findUserByEmail(email: String): Future[Option[User]] =
//    db.run(users.filter(_.email === email).result.headOption)
//}
