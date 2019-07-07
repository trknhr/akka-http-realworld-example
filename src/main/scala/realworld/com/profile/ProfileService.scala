package realworld.com.profile

import realworld.com.core.User
import realworld.com.users.UserStorage
import realworld.com.utils.{DBIOOptional, StorageRunner}

import scala.concurrent.{ ExecutionContext, Future }

class ProfileService(runner: StorageRunner, userStorage: UserStorage)(
    implicit
    executionContext: ExecutionContext
) {
  def getProfile(userId: Long,
                 username: String): Future[Option[ResponseProfile]] =
    runner.run(
      (for {
        p <- DBIOOptional(userStorage.getUserByUsername(username))
        isFollowing <- DBIOOptional(
          userStorage
            .isFollowing(userId, p.id)
            .map(Some(_)))
      } yield {
        ResponseProfile(Profile(p.username, p.bio, p.image, isFollowing))
      }).dbio
    )

  def follow(userId: Long, username: String): Future[Option[ResponseProfile]] =
    runner.runInTransaction(
      (for {
        p <- DBIOOptional(userStorage.getUserByUsername(username))
        _ <- DBIOOptional(userStorage.follow(userId, p.id).map(Some(_)))
      } yield {
        ResponseProfile(Profile(p.username, p.bio, p.image, true))
      }).dbio
    )

  def unfollow(userId: Long,
               username: String): Future[Option[ResponseProfile]] =
    runner.runInTransaction(
      (for {
        p <- DBIOOptional(userStorage.getUserByUsername(username))
        _ <- DBIOOptional(userStorage.unfollow(userId, p.id).map(Some(_)))
      } yield {
        ResponseProfile(Profile(p.username, p.bio, p.image, false))
      }).dbio
    )

  def getFollowees(userId: Long): Future[Seq[User]] =
    runner.run(
      userStorage.getFollowees(userId)
    )
}
