package realworld.com.articles.comments

import realworld.com.utils.DatabaseConnector

import scala.concurrent.ExecutionContext
import scala.concurrent.{ ExecutionContext, Future }

trait CommentStorage {
  def createComment(comment: Comment): Future[Comment]
  def getComments(articleId: Long): Future[Seq[Comment]]
  def deleteComments(commentId: Long): Future[Int]
}

class JdbcCommentStorage(
    val databaseConnector: DatabaseConnector
)(implicit executionContext: ExecutionContext) extends CommentTable with CommentStorage {
  import databaseConnector._
  import databaseConnector.profile.api._

  def createComment(comment: Comment): Future[Comment] = {
    val commentWithId =
      (comments returning (comments)) += comment

    db.run(commentWithId)
  }

  def getComments(articleId: Long): Future[Seq[Comment]] =
    db.run(comments.filter(c => c.articleId === articleId).result)

  def deleteComments(commentId: Long): Future[Int] =
    db.run(comments.filter(c => c.id === commentId).delete)
}
