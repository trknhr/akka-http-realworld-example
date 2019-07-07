package realworld.com.articles.comments

import org.scalatest.time.{ Seconds, Span }
import realworld.com.BaseServiceTest
import realworld.com.articles.JdbcArticleStorage
import realworld.com.test_helpers.{ Articles, Authors, Comments }
import realworld.com.users.JdbcUserStorage
import realworld.com.utils.{ DatabaseCleaner, InMemoryPostgresStorage }

class CommentStorageTest extends BaseServiceTest {
  override def afterEach(): Unit = {
    DatabaseCleaner.cleanDatabase(InMemoryPostgresStorage.databaseConnector)
    super.afterEach()
  }
  "createComment and getComments" when {
    "return comments and create comments" in new Context {
      dbRun(
        for {
          u <- userStorage.saveUser(Authors.normalAuthor)
          commentUser <- userStorage.saveUser(Authors.normalAuthor.copy(email = "second email", username = "second usernam"))
          a <- articleStorage.createArticle(Articles.normalArticle.copy(authorId = u.id))
          _ <- commentStorage.createComment(Comments.normalComment.copy(authorId = commentUser.id, articleId = a.id))
          _ <- commentStorage.createComment(
            Comments.normalComment.copy(id = 2, body = "second comment", authorId = commentUser.id, articleId = a.id)
          )
          comments <- commentStorage.getComments(a.id)
        } yield {
          comments shouldBe Seq(
            Comments.normalComment.copy(articleId = a.id, authorId = commentUser.id),
            Comments.normalComment.copy(id = 2, body = "second comment", articleId = a.id, authorId = commentUser.id)
          )
        }
      )
    }
  }

  "deleteComment" when {
    "delete a comment" in new Context {
      dbRun(for {
        u <- userStorage.saveUser(Authors.normalAuthor)
        a <- articleStorage.createArticle(Articles.normalArticle.copy(authorId = u.id))
        c <- commentStorage.createComment(Comments.normalComment.copy(authorId = u.id, articleId = a.id))
        res <- commentStorage.deleteComments(c.id)
        comments <- commentStorage.getComments(a.id)
      } yield {
        res shouldBe 1
        comments.length shouldBe 0
      })
    }
  }

  trait Context {
    val commentStorage: CommentStorage = new JdbcCommentStorage(
      InMemoryPostgresStorage.databaseConnector
    )
    val articleStorage = new JdbcArticleStorage(
      InMemoryPostgresStorage.databaseConnector
    )
    val userStorage = new JdbcUserStorage(
      InMemoryPostgresStorage.databaseConnector
    )
  }
}

