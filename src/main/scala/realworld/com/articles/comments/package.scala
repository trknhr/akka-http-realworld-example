package realworld.com.articles

import java.sql.Timestamp
import java.util.Date

import com.roundeights.hasher.Implicits._
import realworld.com.profile.Profile

package object comments {
  case class CommentRequest(comment: CommentForJson)
  case class CommentForJson(body: String)
  case class Comment(
    id: Long,
    body: String,
    articleId: Long,
    authorId: Long,
    createdAt: Timestamp,
    updatedAt: Timestamp
  )
  object Comment {
    def create(body: String, articleId: Long, authorId: Long): Comment =
      Comment(
        -1,
        body,
        articleId,
        authorId,
        new Timestamp((new Date).getTime),
        new Timestamp((new Date).getTime)
      )
  }

  case class CommentResponse(
    id: Long,
    createdAt: Timestamp,
    updatedAt: Timestamp,
    body: String,
    username: String,
    author: Profile
  )
}
