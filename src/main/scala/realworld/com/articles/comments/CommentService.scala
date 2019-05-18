package realworld.com.articles.comments

import realworld.com.articles.{ Article, ArticleStorage, TagV }
import realworld.com.core.User
import realworld.com.profile.Profile
import realworld.com.users.UserStorage
import realworld.com.utils.{ FutureOptional, ISO8601 }

import scala.concurrent.{ ExecutionContext, Future }

class CommentService(
    articleStorage: ArticleStorage,
    commentStorage: CommentStorage,
    userStorage: UserStorage
)(implicit executionContext: ExecutionContext) {

  def createComment(
    slug: String,
    userId: Long,
    comment: CommentRequest
  ): Future[Option[CommentResponse]] =
    (for {
      a <- FutureOptional(articleStorage.getArticleBySlug(slug))
      u <- FutureOptional(userStorage.getUser(userId))
      c <- FutureOptional(
        commentStorage
          .createComment(Comment.create(comment.comment.body, a.id, userId))
          .map(Some(_))
      )
    } yield CommentResponse(
      CommentData(
        c.id,
        ISO8601(c.createdAt),
        ISO8601(c.updatedAt),
        c.body,
        u.username,
        Profile(
          u.username,
          u.bio,
          u.image,
          false
        )
      )
    )).future

  def getComments(
    slug: String,
    userId: Long
  ): Future[CommentsResponse] =
    for {
      a <- articleStorage.getArticleBySlug(slug)
      comments <- commentStorage.getComments(
        a.map(b => b.id).getOrElse(-1L)
      )
      users: Seq[User] <- userStorage.getUsers(comments.map(_.authorId))
    } yield CommentsResponse(
      users
        .zip(comments)
        .toList
        .map(
          (a: (User, Comment)) =>
            CommentData(
              a._2.id,
              ISO8601(a._2.createdAt),
              ISO8601(a._2.updatedAt),
              a._2.body,
              a._1.username,
              Profile(
                a._1.username,
                a._1.bio,
                a._1.image,
                false
              )
            )
        )
    )

  def deleteComment(
    slug: String,
    id: Long
  ): Future[Int] =
    commentStorage.deleteComments(id)
}
