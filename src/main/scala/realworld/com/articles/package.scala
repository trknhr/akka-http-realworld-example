package realworld.com

import java.sql.Timestamp
import java.util.Date

import com.roundeights.hasher.Implicits._
import realworld.com.profile.Profile
import realworld.com.utils.ISO8601

package object articles {
  case class ForResponseArticles(
    articles: Seq[ArticleForResponse],
    articlesCount: Int)
  case class ForResponseArticle(article: ArticleForResponse)
  case class ResponseArticle(
    id: Long,
    slug: String,
    title: String,
    description: String,
    body: String,
    authorId: Long,
    tagList: Seq[String],
    createdAt: String,
    updatedAt: String)

  case class TagV(id: Long, name: String)
  object TagV {
    def create(tagName: String): TagV = TagV(-1, tagName)
  }

  case class ArticleTag(id: Long, articleId: Long, tagId: Long)
  case class Favorite(id: Long, userId: Long, favoritedId: Long)
  case class ArticleUpdated(
    title: Option[String],
    description: Option[String],
    body: Option[String])

  case class ArticlePosted(
    title: String,
    description: String,
    body: String,
    tagList: Seq[String]) {
    def create(authorId: Long): Article = {
      Article(
        0,
        slugify(title),
        title,
        description,
        body,
        authorId,
        new Timestamp((new Date).getTime),
        new Timestamp((new Date).getTime))
    }
  }

  case class ArticleForResponse(
    slug: String,
    title: String,
    description: String,
    body: String,
    tagList: Seq[String],
    createdAt: String,
    updatedAt: String,
    favorited: Boolean,
    favoritesCount: Int,
    author: Profile)

  def slugify(title: String): String =
    title.toLowerCase().replaceAll("""\s""", "-")

  case class ArticleRequest(
    tag: Option[String],
    authorName: Option[String],
    favorited: Option[String],
    limit: Option[Long],
    offset: Option[Long])
}
