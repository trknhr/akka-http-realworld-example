package realworld.com.articles

import java.sql.Timestamp
import java.util.Date

import realworld.com.BaseServiceTest
import realworld.com.core.User
import realworld.com.users.{JdbcUserStorage, UserStorage}
import realworld.com.utils.InMemoryPostgresStorage

class ArticleStorageTest extends BaseServiceTest {
  "ArticleStorage" when {
    "getArticles" should {
      "return article by author id" in new Context {
        awaitForResult(for {
          _ <- userStorage.saveUser(author)
          _ <- articleStorage.createArticle(testArticle1)
          articles <- articleStorage.getArticles(
            ArticleRequest(authorName = Some("author"),
                           tag = None,
                           favorited = None,
                           limit = 10,
                           offset = 0))
        } yield articles.head shouldBe testArticle1.copy(id = 1))
      }
    }
    "getArticleBySlug" should {
      "return article by slug" in new Context {
        awaitForResult(for {
          _ <- userStorage.saveUser(author)
          _ <- articleStorage.createArticle(testArticle1)
          articles <- articleStorage.getArticleBySlug(
            "title-one"
          )
        } yield articles.head shouldBe testArticle1.copy(id = 1))
      }
    }
  }

  trait Context {
    val articleStorage: ArticleStorage = new JdbcArticleStorage(
      InMemoryPostgresStorage.databaseConnector)
    val userStorage: UserStorage = new JdbcUserStorage(
      InMemoryPostgresStorage.databaseConnector)

    val testArticle1 = Article(0,
                               "title-one",
                               "tile-one",
                               "test description",
                               "test body",
                               1,
                               createdAt = currentWhenInserting,
                               updatedAt = currentWhenInserting)
    val author = User(1,
                      "author",
                      "test",
                      "test",
                      None,
                      image = None,
                      createdAt = currentWhenInserting,
                      updatedAt = currentWhenInserting)

    case class TestUser(userId: Long,
                        username: String,
                        email: String,
                        password: String)
  }

}
