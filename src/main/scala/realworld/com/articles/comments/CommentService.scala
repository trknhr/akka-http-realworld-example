package realworld.com.articles.comments

import realworld.com.articles.{ Article, ArticleStorage, TagV }
import realworld.com.core.User
import realworld.com.profile.Profile
import realworld.com.users.UserStorage
import realworld.com.utils.FutureOptional

import scala.concurrent.{ ExecutionContext, Future }
import realworld.com.utils.MonadTransformers._

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
      c.id,
      c.createdAt,
      c.updatedAt,
      c.body,
      u.username,
      Profile(
        u.username,
        u.bio,
        u.image,
        false
      )
    )).future

  def getComments(
    slug: String,
    userId: Long
  ): Future[Seq[CommentResponse]] =
    for {
      a <- articleStorage.getArticleBySlug(slug)
      comments: Seq[Comment] <- commentStorage.getComments(a.map(b => b.id).getOrElse(-1L))
      users: Seq[User] <- userStorage.getUsers(comments.map(_.authorId))
    } yield users.zip(comments).toList.map((a: (User, Comment)) =>
      CommentResponse(
        a._2.id,
        a._2.createdAt,
        a._2.updatedAt,
        a._2.body,
        a._1.username,
        Profile(
          a._1.username,
          a._1.bio,
          a._1.image,
          false
        )
      ))

  def deleteComment(
    id: Long
  ): Future[Int] =
    commentStorage.deleteComments(id)
}
