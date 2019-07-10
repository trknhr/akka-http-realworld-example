package realworld.com.articles

import java.sql.Timestamp
import java.util.Date

import realworld.com.utils.DatabaseConnector

import slick.jdbc.PostgresProfile.api.{
  DBIO => _,
  MappedTo => _,
  Rep => _,
  TableQuery => _,
  _
}
import slick.lifted.TableQuery
case class Article(
  id: Long,
  slug: String,
  title: String,
  description: String,
  body: String,
  authorId: Long,
  createdAt: Timestamp,
  updatedAt: Timestamp)

trait ArticleTable {

  class Articles(tag: Tag) extends Table[Article](tag, "articles") {
    def currentWhenInserting = new Timestamp((new Date).getTime)
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def slug = column[String]("slug")
    def title = column[String]("title")
    def description = column[String]("description")
    def body = column[String]("body")
    def authorId = column[Long]("author_id")

    def createdAt =
      column[Timestamp]("created_at", O.Default(currentWhenInserting))

    def updatedAt =
      column[Timestamp]("updated_at", O.Default(currentWhenInserting))

    def * =
      (id, slug, title, body, description, authorId, createdAt, updatedAt) <> ((Article.apply _).tupled, Article.unapply)
  }

  protected val articles = TableQuery[Articles]
}
