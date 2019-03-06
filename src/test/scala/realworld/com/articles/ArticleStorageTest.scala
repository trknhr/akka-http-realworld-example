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
  "ArticleStorage" when {
    "getArticles" should {
      "return article by author id" in new Context {
        awaitForResult(
          for {
            user <- userStorage.saveUser(author)
            users <- userStorage.getUsers()
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
          } yield articles.head shouldBe testArticle1.copy(id = 1)
        )
      }
    }
    "getArticleBySlug" should {
      "return article by slug" in new Context {
        awaitForResult(for {
          u <- userStorage.saveUser(author)
          _ <- articleStorage.createArticle(testArticle1.copy(authorId = u.id))
          article <- articleStorage.getArticleBySlug(
            "title-one"
          )
        } yield {
          article.foreach(a => {
            article.head shouldBe testArticle1.copy(id = a.id, authorId = u.id)
          })
        })
      }
    }
    //    "updateArticle" should {
    //      "update an exisiting article" in new Context {
    //
    //      }
    //    }
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
