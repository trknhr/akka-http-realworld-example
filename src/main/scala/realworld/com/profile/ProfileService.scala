package realworld.com.profile

import scala.concurrent.{ExecutionContext, Future}
import realworld.com.utils.MonadTransformers._

/**
  * Created by kunihiro on 2018/07/20.
  */
class ProfileService(profileStorage: ProfileStorage, secretKey: String) (implicit executionContext: ExecutionContext) {
  def getProfile(userId: Long, username: String): Future[Option[Profile]] =
    profileStorage.getProfile(username).flatMapTFuture(p =>
      profileStorage.isFollowing(userId, p.id).map(isFollowing =>
        Profile(p.username, p.bio, p.image, isFollowing)
      )
    )
}
