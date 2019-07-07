package realworld.com.tags

import realworld.com.utils.StorageRunner

import scala.concurrent.{ ExecutionContext, Future }

class TagService(
    runner: StorageRunner,
    tagStorage: TagStorage
)(implicit executionContext: ExecutionContext) {
  def getTags(): Future[ResponseTags] = runner.run(tagStorage.getTags().map(ResponseTags))
}
