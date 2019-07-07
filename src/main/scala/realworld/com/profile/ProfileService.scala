package realworld.com.profile

import realworld.com.core.User
import realworld.com.users.UserStorage
import realworld.com.utils.FutureOptional

import scala.concurrent.{ ExecutionContext, Future }

class ProfileService(userStorage: UserStorage)(
    implicit
    executionContext: ExecutionContext
) {
  def getProfile(
    userId: Long,
    username: String
  ): Future[Option[ResponseProfile]] =
    (for {
      p <- FutureOptional(userStorage.getUserByUsername(username))
      isFollowing <- FutureOptional(
        userStorage
          .isFollowing(userId, p.id)
          .map(Some(_))
      )
    } yield {
      ResponseProfile(Profile(p.username, p.bio, p.image, isFollowing))
    }).future

  def follow(userId: Long, username: String): Future[Option[ResponseProfile]] =
    (for {
      p <- FutureOptional(userStorage.getUserByUsername(username))
      _ <- FutureOptional(userStorage.follow(userId, p.id).map(Some(_)))
    } yield {
      ResponseProfile(Profile(p.username, p.bio, p.image, true))
    }).future

  def unfollow(
    userId: Long,
    username: String
  ): Future[Option[ResponseProfile]] =
    (for {
      p <- FutureOptional(userStorage.getUserByUsername(username))
      _ <- FutureOptional(userStorage.unfollow(userId, p.id).map(Some(_)))
    } yield {
      ResponseProfile(Profile(p.username, p.bio, p.image, false))
    }).future

  def getFollowees(userId: Long): Future[Seq[User]] =
    userStorage.getFollowees(userId)
}
