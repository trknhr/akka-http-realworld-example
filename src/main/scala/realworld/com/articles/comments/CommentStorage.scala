package realworld.com.articles.comments

import realworld.com.utils.DatabaseConnector

import scala.concurrent.ExecutionContext
import scala.concurrent.{ ExecutionContext, Future }

trait CommentStorage {
  def createComment(comment: Comment): Future[Comment]
}

class JdbcCommentStorage(
    val databaseConnector: DatabaseConnector
)(implicit executionContext: ExecutionContext) extends CommentTable {
  import databaseConnector._
  import databaseConnector.profile.api._

  def createComment(comment: Comment): Future[Comment] = {
    val commentWithId =
      (comments returning (comments)) += comment

    db.run(commentWithId)
  }
}
