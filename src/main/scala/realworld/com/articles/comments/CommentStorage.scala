package realworld.com.articles.comments

import realworld.com.utils.DatabaseConnector
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api.{
  DBIO => _,
  MappedTo => _,
  Rep => _,
  TableQuery => _,
  _
}
import scala.concurrent.ExecutionContext.Implicits.global

trait CommentStorage {
  def createComment(comment: Comment): DBIO[Comment]
  def getComments(articleId: Long): DBIO[Seq[Comment]]
  def deleteComments(commentId: Long): DBIO[Int]
}

class JdbcCommentStorage extends CommentTable with CommentStorage {

  def createComment(comment: Comment): DBIO[Comment] =
    (comments returning (comments)) += comment

  def getComments(articleId: Long): DBIO[Seq[Comment]] =
    comments.filter(c => c.articleId === articleId).result

  def deleteComments(commentId: Long): DBIO[Int] =
    comments.filter(c => c.id === commentId).delete
}
