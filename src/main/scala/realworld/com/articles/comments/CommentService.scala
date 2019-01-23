package realworld.com.articles.comments

import realworld.com.articles.{ Article, ArticleStorage, TagV }
import realworld.com.core.User
import realworld.com.profile.Profile
import realworld.com.users.UserStorage

import scala.concurrent.{ ExecutionContext, Future }
import realworld.com.utils.MonadTransformers._

class CommentService(
    articleStorage: ArticleStorage,
    commentStorage: CommentStorage,
    userStorage: UserStorage
)(implicit executionContext: ExecutionContext) {

  def hoge: Future[Option[Int]] = Future(Some(1))
  def createComment(
    slug: String,
    userId: Long,
    comment: CommentRequest
  ): Future[Option[CommentResponse]] =
    articleStorage
      .getArticleBySlug(slug)
      .flatMapTFuture {
        a => getCommentWithProfile(a, comment.comment.body, userId)
        //          userStorage
        //            .getUser(userId)
        //            .flatMapTFuture(
        //              u => getCommentWithProfile(a, u, comment.comment.body, userId)
        //                commentStorage
        //                  .createComment(Comment.create(comment.comment.body, a.id, userId))
        //                  .map(
        //                    c =>
        //                      Some(
        //                        CommentResponse(
        //                          c.id,
        //                          c.createdAt,
        //                          c.updatedAt,
        //                          c.body,
        //                          u.username,
        //                          Profile(
        //                            u.username,
        //                            u.bio,
        //                            u.image,
        //                            false
        //                          )
        //                        )
        //                      )
        //                  )
        //        )
      }

  def getCommentWithProfile(
    a: Article,
    //                          u: User,
    body: String,
    userId: Long
  ) =
    userStorage
      .getUser(userId)
      .flatMapTFuture(u =>
        for {
          c <- commentStorage.createComment(Comment.create(body, a.id, userId))
        } yield {
          Some(CommentResponse(
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
          ))
        })

}
