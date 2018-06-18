package users

import utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

sealed trait UserStorage {
  def getUsers(): Future[Seq[User]]
  def register(userRegistration: User): Future[User]
}
/**
  * Created by kunihiro on 2018/06/13.
  */
class JdbcUserStorage (
  val databaseConnector: DatabaseConnector
  )(implicit executionContext: ExecutionContext) extends UserProfileTable with UserStorage {
  import databaseConnector._
  import databaseConnector.profile.api._



  def getUsers(): Future[Seq[User]] = db.run(users.result)

  def register(user: User): Future[User] = {
    val userWithId =
      (users returning users.map(_.id) into ((u, id) => u.copy(id = id + 1) ) ) += user
//    val action = insertQuery += user

//    db.run(action).map(a => user)
    db.run(userWithId)
  }
}