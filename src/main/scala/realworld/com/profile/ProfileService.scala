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
}
