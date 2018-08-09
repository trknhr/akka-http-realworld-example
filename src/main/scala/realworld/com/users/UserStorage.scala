package realworld.com.users


import realworld.com.core.User
import realworld.com.utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

sealed trait UserStorage {
  def getUsers(): Future[Seq[User]]
  def getUser(userId: Long): Future[Option[User]]
  def register(userRegistration: User): Future[User]
  def findUserByEmail(email: String): Future[Option[User]]
  def saveUser(user: User): Future[User];
}

class JdbcUserStorage (
  val databaseConnector: DatabaseConnector
  )(implicit executionContext: ExecutionContext) extends UserProfileTable with UserStorage {
  import databaseConnector._
  import databaseConnector.profile.api._



  def getUsers(): Future[Seq[User]] = db.run(users.result)

  def getUser(userId: Long): Future[Option[User]] =
    db.run(users.filter(_.id === userId).result.headOption)

  def register(user: User): Future[User] = {
    val userWithId =
      (users returning users.map(_.id) into ((u, id) => u.copy(id = id + 1) ) ) += user

      db.run(userWithId)
  }

  def saveUser(user: User): Future[User] =
    db.run(users.insertOrUpdate(user)).map(_ => user)

  def findUserByEmail(email: String): Future[Option[User]] =
    db.run(users.filter(_.email === email).result.headOption)
}