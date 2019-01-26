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
    (for {
      a <- FutureOptional(articleStorage.getArticleBySlug(slug))
      comments <- commentStorage.getComments(a.id)
      users <- userStorage.getUsers(comments.map(_.authorId))
    } yield
     users.zip(comments).map( (u, c) =>
      CommentResponse(
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
    )}).future
  //    for {
  //      a <- articleStorage.getArticleBySlug(slug)
  //    } yield
  //      a.flatMap { a =>
  //        for {
  //          comments <- commentStorage.getComments(a.id)
  //          users <- userStorage.getUsers(comments.map(_.authorId))
  //        } yield
  //          for ((u, c) <- users zip comments)
  //            yield
  //              CommentResponse(
  //                c.id,
  //                c.createdAt,
  //                c.updatedAt,
  //                c.body,
  //                u.username,
  //                Profile(
  //                  u.username,
  //                  u.bio,
  //                  u.image,
  //                  false
  //                )
  //              )
  //
  //      }
  //    articleStorage.getArticleBySlug(slug).flatMap { optionA =>
  //      for {
  //        a <- optionA
  //        comments <- commentStorage.getComments(a.id)
  //        users <- userStorage.getUsers(comments.map(_.authorId))
  //      } yield for ((u, c) <- users zip comments) yield CommentResponse(
  //        c.id,
  //        c.createdAt,
  //        c.updatedAt,
  //        c.body,
  //        u.username,
  //        Profile(
  //          u.username,
  //          u.bio,
  //          u.image,
  //          false
  //        )
  //      )
  //    }

  //        c => CommentResponse(
  //        c.id,
  //        c.createdAt,
  //        c.updatedAt,
  //        c.body,
  //        u.username,
  //        Profile(
  //          u.username,
  //          u.bio,
  //          u.image,
  //          false
  //        )

  //      )
  //    )
  //    (for {
  //      a <- FutureOptional(articleStorage.getArticleBySlug(slug))
  //      b <-
  ////      u <- FutureOptional(userStorage.getUser(userId))
  //    )
}
