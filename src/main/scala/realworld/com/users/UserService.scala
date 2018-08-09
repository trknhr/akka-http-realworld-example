package realworld.com.users

import scala.concurrent.{ExecutionContext, Future}
import com.roundeights.hasher.Implicits._
import pdi.jwt.{Jwt, JwtAlgorithm}
import realworld.com.core._
import io.circe.syntax._
import io.circe.generic.auto._
import realworld.com.utils.MonadTransformers._

class UserService(userStorage: UserStorage, secretKey: String)(implicit executionContext: ExecutionContext) {
  def getUsers(): Future[Seq[User]] =
    userStorage.getUsers()

  def getCurrentUser(userId: Long): Future[Option[User]] =
    userStorage.getUser(userId)

  def updateUser(id: Long, userUpdate: UserUpdate) : Future[Option[User]] =
    userStorage
      .getUser(id)
      .mapT(userUpdate.merge)
      .flatMapTFuture(userStorage.saveUser)

  def register(userRegistration: UserRegistration): Future[User] =
    userStorage.register(userRegistration.create())

  def login(email: String, password: String): Future[Option[UserWithToken]] =
    userStorage
      .findUserByEmail(email)
      .filterT(_.password == password.sha256.hex)
      .mapT(user => UserWithToken(user.username, user.email, user.bio, user.image, encodeToken(user.id)))

  private def encodeToken(userId: Long): AuthToken = {
    Jwt.encode(AuthTokenContent(userId).asJson.noSpaces, secretKey, JwtAlgorithm.HS256)
  }
}



