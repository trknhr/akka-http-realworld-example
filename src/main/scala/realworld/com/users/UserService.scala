package realworld.com.users

import scala.concurrent.{ ExecutionContext, Future }
import com.roundeights.hasher.Implicits._
import pdi.jwt.{ Jwt, JwtAlgorithm }
import realworld.com.core._
import io.circe.syntax._
import io.circe.generic.auto._
import realworld.com.utils.{DBIOOptional, StorageRunner}

class UserService(runner: StorageRunner, userStorage: UserStorage, secretKey: String)(
    implicit
    executionContext: ExecutionContext
) {
  def getUsers(): Future[Seq[User]] =
    runner.run(
      userStorage.getUsers()
    )

  def getCurrentUser(userId: Long): Future[Option[ResponseUser]] =
    runner.run(
      (for{
        a <- DBIOOptional(userStorage.getUser(userId))
      } yield {
        ResponseUser(
          UserWithToken(a.username, a.email, a.bio, a.image, encodeToken(a.id))
        )
    }).dbio)

  def updateUser(
    id: Long,
    userUpdate: UserUpdate
  ): Future[Option[ResponseUser]] =
    runner.runInTransaction(
    (for {
      u <- DBIOOptional(userStorage.getUser(id))
      a <- DBIOOptional(
        userStorage.saveUser(userUpdate.merge(u)).map(Some(_)))
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
    }).dbio)

  def register(userRegistration: UserRegistration): Future[ResponseUser] =
    runner.runInTransaction(
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
        ))
    })

  def login(email: String, password: String): Future[Option[ResponseUser]] =
    runner.run(
    (for {
      user <- DBIOOptional(userStorage.findUserByEmail(email, password.sha256.hex))
    } yield {
      ResponseUser(
        UserWithToken(
          user.username,
          user.email,
          user.bio,
          user.image,
          encodeToken(user.id)
        ))
    }).dbio)

  private def encodeToken(userId: Long): AuthToken = {
    Jwt.encode(
      AuthTokenContent(userId).asJson.noSpaces,
      secretKey,
      JwtAlgorithm.HS256
    )
  }
}
