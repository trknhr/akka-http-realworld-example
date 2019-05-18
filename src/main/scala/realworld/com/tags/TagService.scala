package realworld.com.tags

import realworld.com.articles.TagV
import realworld.com.core.User
import realworld.com.profile.Profile
import realworld.com.users.UserStorage

import scala.concurrent.{ ExecutionContext, Future }
import realworld.com.utils.MonadTransformers._

class TagService(
    tagStorage: TagStorage
)(implicit executionContext: ExecutionContext) {
  def getTags(): Future[ResponseTags] = tagStorage.getTags().map(ResponseTags)
}
