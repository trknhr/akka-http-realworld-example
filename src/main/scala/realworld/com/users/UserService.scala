package realworld.com.users

import scala.concurrent.{ ExecutionContext, Future }
import com.roundeights.hasher.Implicits._
import pdi.jwt.{ Jwt, JwtAlgorithm }
import realworld.com.core._
import io.circe.syntax._
import io.circe.generic.auto._
import realworld.com.utils.MonadTransformers._

class UserService(userStorage: UserStorage, secretKey: String)(
    implicit
    executionContext: ExecutionContext
) {
  def getUsers(): Future[Seq[User]] =
    userStorage.getUsers()

  def getCurrentUser(userId: Long): Future[Option[ResponseUser]] =
    userStorage
      .getUser(userId)
      .mapT(a => {
        ResponseUser(
          UserWithToken(a.username, a.email, a.bio, a.image, encodeToken(a.id))
        )
      })

  def updateUser(
    id: Long,
    userUpdate: UserUpdate
  ): Future[Option[ResponseUser]] =
    userStorage
      .getUser(id)
      .mapT(userUpdate.merge)
      .flatMapTFuture(userStorage.saveUser)
      .mapT(
        a =>
          ResponseUser(
            UserWithToken(
              a.username,
              a.email,
              a.bio,
              a.image,
              encodeToken(a.id)
            )
          )
      )

  def register(userRegistration: UserRegistration): Future[ResponseUser] =
    userStorage
      .register(userRegistration.create())
      .map(
        a =>
          ResponseUser(
            UserWithToken(
              a.username,
              a.email,
              a.bio,
              a.image,
              encodeToken(a.id)
            )
          )
      )

  def login(email: String, password: String): Future[Option[ResponseUser]] =
    userStorage
      .findUserByEmail(email)
      .filterT(_.password == password.sha256.hex)
      .mapT(
        user =>
          ResponseUser(
            UserWithToken(
              user.username,
              user.email,
              user.bio,
              user.image,
              encodeToken(user.id)
            )
          )
      )

  private def encodeToken(userId: Long): AuthToken = {
    Jwt.encode(
      AuthTokenContent(userId).asJson.noSpaces,
      secretKey,
      JwtAlgorithm.HS256
    )
  }
}
