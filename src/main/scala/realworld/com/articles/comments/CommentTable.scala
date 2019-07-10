package realworld.com.articles.comments

import realworld.com.utils.DatabaseConnector
import java.sql.Timestamp
import java.util.Date
import slick.jdbc.PostgresProfile.api.{ DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _ }
import slick.lifted.TableQuery

trait CommentTable {

  class Comments(tag: Tag) extends Table[Comment](tag, "comments") {
    def currentWhenInserting = new Timestamp((new Date).getTime)

    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def body = column[String]("body")
    def authorId = column[Long]("author_id")
    def articleId = column[Long]("article_id")
    def createdAt =
      column[Timestamp]("created_at", O.Default(currentWhenInserting))

    def updatedAt =
      column[Timestamp]("updated_at", O.Default(currentWhenInserting))
    def * =
      (id, body, articleId, authorId, createdAt, updatedAt) <> ((Comment.apply _).tupled, Comment.unapply)

  }

  protected val comments = TableQuery[Comments]

}
