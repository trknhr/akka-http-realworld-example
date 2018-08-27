package realworld.com.profile

import realworld.com.users.UserStorage

import scala.concurrent.{ExecutionContext, Future}
import realworld.com.utils.MonadTransformers._

class ProfileService(userStorage: UserStorage)(implicit executionContext: ExecutionContext) {
  def getProfile(userId: Long, username: String): Future[Option[Profile]] =
    userStorage.getUserByUsername(username).flatMapTFuture(p =>
      userStorage.isFollowing(userId, p.id).map(isFollowing =>
        Profile(p.username, p.bio, p.image, isFollowing)
      )
    )

  def follow(userId: Long, username: String): Future[Int] =
    userStorage.getUserByUsername(username).flatMapTFuture(p =>
      userStorage.follow(userId, p.id)
    ).map(a => 1)
}