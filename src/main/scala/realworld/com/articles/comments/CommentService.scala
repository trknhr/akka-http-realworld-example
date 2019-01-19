package realworld.com.articles.comments

import realworld.com.articles.ArticleStorage
import realworld.com.profile.Profile
import realworld.com.users.UserStorage

import scala.concurrent.{ExecutionContext, Future}
import realworld.com.utils.MonadTransformers._

class CommentService(
    articleStorage: ArticleStorage,
    commentStorage: CommentStorage,
    userStorage: UserStorage
)(implicit executionContext: ExecutionContext) {
  def createComment(slug: String, userId: Long, comment: CommentRequest): Future[CommentResponse] =
    articleStorage.getArticleBySlug(slug).flatMapTFuture(a => {
      userStorage.getUser(userId).flatMapTFuture(u =>
        for {
          c <- commentStorage.createComment(Comment.create(comment.body, a.id, userId))
        } yield CommentResponse(c.id, c.createdAt, c.updatedAt, c.body, u.username, Profile(
          u.username,
          u.bio,
          u.image,
          false
        ))
      )
    }).
}
