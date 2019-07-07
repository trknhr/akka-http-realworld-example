package realworld.com.users

import scala.concurrent.{ ExecutionContext, Future }
import com.roundeights.hasher.Implicits._
import pdi.jwt.{ Jwt, JwtAlgorithm }
import realworld.com.core._
import io.circe.syntax._
import io.circe.generic.auto._
import realworld.com.utils.FutureOptional

class UserService(userStorage: UserStorage, secretKey: String)(
    implicit
    executionContext: ExecutionContext
) {
  def getUsers(): Future[Seq[User]] =
    userStorage.getUsers()

  def getCurrentUser(userId: Long): Future[Option[ResponseUser]] =
    (for {
      a <- FutureOptional(userStorage.getUser(userId))
    } yield {
      ResponseUser(
        UserWithToken(a.username, a.email, a.bio, a.image, encodeToken(a.id))
      )
    }).future

  def updateUser(
    id: Long,
    userUpdate: UserUpdate
  ): Future[Option[ResponseUser]] =
    (for {
      u <- FutureOptional(userStorage.getUser(id))
      a <- FutureOptional(
        userStorage.saveUser(userUpdate.merge(u)).map(Some(_))
      )
    } yield {
      ResponseUser(
        UserWithToken(
          a.username,
          a.email,
          a.bio,
          a.image,
          encodeToken(a.id)
        )
      )
    }).future

  def register(userRegistration: UserRegistration): Future[ResponseUser] =
    for {
      a <- userStorage.register(userRegistration.create())
    } yield {
      ResponseUser(
        UserWithToken(
          a.username,
          a.email,
          a.bio,
          a.image,
          encodeToken(a.id)
        )
      )
    }

  def login(email: String, password: String): Future[Option[ResponseUser]] =
    (for {
      user <- FutureOptional(userStorage.findUserByEmail(email))
      _ <- FutureOptional(
        if (user.password == password.sha256.hex) Future { Some(user) } else Future { None }
      )
    } yield {
      ResponseUser(
        UserWithToken(
          user.username,
          user.email,
          user.bio,
          user.image,
          encodeToken(user.id)
        )
      )
    }).future

  private def encodeToken(userId: Long): AuthToken = {
    Jwt.encode(
      AuthTokenContent(userId).asJson.noSpaces,
      secretKey,
      JwtAlgorithm.HS256
    )
  }
}
