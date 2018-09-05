package realworld.com

import java.sql.Timestamp

package object articles {
  case class Article(id: Long, slug: String, title: String, description: String, body: String, authorId: Long, createdAt: Timestamp, updatedAt: Timestamp)
}
