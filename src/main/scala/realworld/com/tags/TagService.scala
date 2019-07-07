package realworld.com.tags

import scala.concurrent.{ ExecutionContext, Future }

class TagService(
    tagStorage: TagStorage
)(implicit executionContext: ExecutionContext) {
  def getTags(): Future[ResponseTags] = tagStorage.getTags().map(ResponseTags)
}
