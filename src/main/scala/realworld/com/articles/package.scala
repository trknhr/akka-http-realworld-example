package realworld.com

import java.sql.Timestamp
import java.util.Date

import com.roundeights.hasher.Implicits._

package object articles {
  case class Article(id: Long, slug: String, title: String, description: String, body: String, authorId: Long, createdAt: Timestamp, updatedAt: Timestamp)

  case class ArticlePosted(title: String, description: String, body: String) {
    def create(authorId: Long): Article = {
      Article(0, slugify(title), title,  description, body, authorId, new Timestamp((new Date).getTime), new Timestamp((new Date).getTime))
    }
  }
  private def slugify(title: String): String =
    title.toLowerCase().replaceAll("""\s""", "-")


  case class ArticleRequest(tag: Option[String], authorName: Option[String], favorited: Option[String], limit: Long = 100, offset: Long = 0)
}
