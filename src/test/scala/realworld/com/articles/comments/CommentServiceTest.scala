package realworld.com.articles.comments

import org.scalamock.scalatest.MockFactory
import realworld.com.BaseServiceTest
import realworld.com.articles.ArticleStorage
import realworld.com.test_helpers.{ Articles, Authors, Comments, Dates }
import realworld.com.users.UserStorage
import realworld.com.utils.{ InMemoryPostgresStorage, StorageRunner }
import slick.dbio.DBIO

class CommentServiceTest extends BaseServiceTest with MockFactory {

  "CommentService" when {
    "createComment" should {
      "create a comment correctly" in new Context {
        val testSlug = "test-slug"
        val testUserId = 2
        val testBody = "testbody"
        val testComment = CommentRequest(CommentForJson(testBody));

        (articleStorage.getArticleBySlug _)
          .expects(testSlug) returning DBIO.successful(Some(Articles.normalArticle))
        (userStorage.getUser _)
          .expects(Articles.normalArticle.authorId) returning DBIO.successful(
            Some(Authors.normalAuthor)
          )
        (commentStorage.createComment _).expects(*) returning DBIO.successful(
          Comment(
            1,
            testBody,
            1,
            Authors.normalAuthor.id,
            Dates.currentWhenInserting,
            Dates.currentWhenInserting
          )
        )

        (userStorage.isFollowing _).expects(
          testUserId,
          Articles.normalArticle.authorId
        ) returning DBIO.successful(true)

        whenReady(
          for (
            c <- commentService
              .createComment(testSlug, testUserId, testComment)
          ) yield c
        ) { c =>
            c.foreach(cr => {
              cr.comment.body shouldBe testBody
              cr.comment.id shouldBe 1
              cr.comment.author.username shouldBe Authors.normalAuthor.username
              cr.comment.author.bio shouldBe Authors.normalAuthor.bio
              cr.comment.author.image shouldBe Authors.normalAuthor.image
              cr.comment.author.following shouldBe true
            })
          }
      }
    }
    "getComments" should {
      "get comments" in new Context {
        val testUserId = 2
        val testSlug = "test-slug"
        (articleStorage.getArticleBySlug _).expects(testSlug) returning DBIO.successful {
          Some(Articles.normalArticle)
        }
        (commentStorage.getComments _).expects(*) returning DBIO.successful(
          Comments.comments
        )
        (userStorage.getUsersByUserIds _)
          .expects(Comments.comments.map(_.authorId)) returning DBIO.successful(
            Seq(
              Authors.normalAuthor.copy(id = 3, username = "first"),
              Authors.normalAuthor.copy(id = 4, username = "second"),
              Authors.normalAuthor.copy(id = 5, username = "third")
            )
          )
        (userStorage.followingUsers _).expects(
          testUserId,
          Comments.comments.map(_.authorId)
        ) returning DBIO.successful(
            Seq(3, 4)
          )

        whenReady(
          for (res <- commentService.getComments(testSlug, testUserId))
            yield res
        ) { res =>
            res.comments.length shouldBe 3
            res.comments.map(_.body) shouldBe Comments.comments.map(_.body)
            res.comments.map(a => (a.author.username, a.author.following)) shouldBe Seq(("first", true), ("second", true), ("third", false))
          }
      }
    }
  }
  trait Context {
    val articleStorage = mock[ArticleStorage]
    val commentStorage = mock[CommentStorage]
    val userStorage = mock[UserStorage]
    val storageRunner = new StorageRunner(
      InMemoryPostgresStorage.databaseConnector
    )
    val commentService =
      new CommentService(storageRunner, articleStorage, commentStorage, userStorage)
  }
}
