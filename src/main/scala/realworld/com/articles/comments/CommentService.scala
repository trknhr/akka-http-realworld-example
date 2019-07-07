package realworld.com.articles.comments

import realworld.com.articles.ArticleStorage
import realworld.com.core.User
import realworld.com.profile.Profile
import realworld.com.users.UserStorage
import realworld.com.utils.{ DBIOOptional, ISO8601, StorageRunner }

import scala.concurrent.{ ExecutionContext, Future }

class CommentService(
  runner: StorageRunner,
  articleStorage: ArticleStorage,
  commentStorage: CommentStorage,
  userStorage: UserStorage)(implicit executionContext: ExecutionContext) {

  def createComment(
    slug: String,
    userId: Long,
    comment: CommentRequest): Future[Option[CommentResponse]] =
    runner.runInTransaction(
      (for {
        a <- DBIOOptional(articleStorage.getArticleBySlug(slug))
        u <- DBIOOptional(userStorage.getUser(a.authorId))
        c <- DBIOOptional(
          commentStorage
            .createComment(Comment.create(comment.comment.body, a.id, userId))
            .map(Some(_)))
        follow <- DBIOOptional(
          userStorage.isFollowing(userId, a.authorId).map(Some(_)))
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
            follow)))).dbio)

  def getComments(
    slug: String,
    userId: Long): Future[CommentsResponse] =
    runner.run(
      for {
        a <- articleStorage.getArticleBySlug(slug)
        comments <- commentStorage.getComments(
          a.map(b => b.id).getOrElse(-1L))
        users <- userStorage.getUsersByUserIds(comments.map(_.authorId))
        follows <- userStorage.followingUsers(userId, comments.map(_.authorId))
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
                  follows.toSet.contains(a._2.authorId))))))

  def deleteComment(
    slug: String,
    id: Long): Future[Int] =
    runner.runInTransaction(
      commentStorage.deleteComments(id))
}
