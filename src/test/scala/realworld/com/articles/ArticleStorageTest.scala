package realworld.com.articles

import java.sql.Timestamp
import java.util.Date

import realworld.com.{ BaseServiceTest }
import realworld.com.core.User
import realworld.com.users.{ JdbcUserStorage, UserStorage }
import realworld.com.utils.{ DatabaseCleaner, InMemoryPostgresStorage }

class ArticleStorageTest extends BaseServiceTest {
  override def beforeEach(): Unit = {
    DatabaseCleaner.cleanDatabase(InMemoryPostgresStorage.databaseConnector)
    super.beforeEach()
  }

  "getArticles" when {
    "return article by author id" in new Context {
      awaitForResult(
        for {
          user <- userStorage.saveUser(author)
          article <- articleStorage.createArticle(
            testArticle1.copy(authorId = user.id)
          )
          articles <- articleStorage.getArticles(
            ArticleRequest(
              authorName = Some(user.username),
              tag = None,
              favorited = None,
              limit = 10,
              offset = 0
            )
          )
          users <- userStorage.getUsers()
        } yield {
          articles.head shouldBe testArticle1.copy(id = 1, authorId = user.id)
        }
      )
    }
    "getArticleBySlug" when {
      "return article by slug" in new Context {
        awaitForResult(for {
          u <- userStorage.saveUser(author)
          _ <- articleStorage.createArticle(testArticle1.copy(authorId = u.id))
          article <- articleStorage.getArticleBySlug(
            "title-one"
          )
        } yield {
          article.foreach(a => {
            a shouldBe testArticle1.copy(id = a.id, authorId = u.id)
          })
        })
      }
    }
    "updateArticle" when {
      "update an existing article" in new Context {
        val updatedBody = "updated body"
        awaitForResult(for {
          u <- userStorage.saveUser(author)
          a <- articleStorage.createArticle(testArticle1.copy(authorId = u.id))
        } yield {
          for {
            _ <- articleStorage.updateArticle(a.copy(body = updatedBody))
            article <- articleStorage.getArticleBySlug(
              "title-one"
            )
          } yield article.foreach(expect => {
            expect.body shouldBe updatedBody
          })
        })
      }
    }
    "favorite" when {
      "favoriteArticle" should {
        "should set favorite" in new Context {
          awaitForResult(for {
            u <- userStorage.saveUser(author)
            a <- articleStorage.createArticle(testArticle1.copy(authorId = u.id))
          } yield for {
            f <- articleStorage.favoriteArticle(u.id, a.id)
          } yield {
            println(f)

          })
        }
      }
    }
  }

  trait Context {
    val articleStorage: ArticleStorage = new JdbcArticleStorage(
      InMemoryPostgresStorage.databaseConnector
    )
    val userStorage: UserStorage = new JdbcUserStorage(
      InMemoryPostgresStorage.databaseConnector
    )

    val testArticle1 = Article(
      0,
      "title-one",
      "tile-one",
      "test description",
      "test body",
      1,
      createdAt = currentWhenInserting,
      updatedAt = currentWhenInserting
    )
    val author = User(
      1,
      "author",
      "test",
      "test",
      None,
      image = None,
      createdAt = currentWhenInserting,
      updatedAt = currentWhenInserting
    )

    case class TestUser(
      userId: Long,
      username: String,
      email: String,
      password: String
    )
  }

}
