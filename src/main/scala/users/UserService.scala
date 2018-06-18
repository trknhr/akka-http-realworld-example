package users

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by kunihiro on 2018/06/13.
  */
class UserService(userStorage: UserStorage)(implicit executionContext: ExecutionContext) {
  def getUsers(): Future[Seq[User]] =
    userStorage.getUsers()

  def register(userRegistration: UserRegistration): Future[User] =
    userStorage.register(userRegistration.create())
}
