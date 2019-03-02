package realworld.com.articles

import org.scalamock.scalatest.MockFactory
import realworld.com.BaseServiceTest
import realworld.com.core.User
import realworld.com.users.UserStorage
import scala.concurrent.Future

class ArticleServiceTest extends BaseServiceTest with MockFactory {
  "ArticleService" when {
    "getArticles" should {
      "return articles by username" in new Context {
        val article1 = Article(0, "slug", "title", "description", "body", 1, currentWhenInserting, currentWhenInserting)
        var request = ArticleRequest(tag = None, authorName = Some("testAuthor"), favorited = None)
        (articleStorage.getArticles _).expects(request) returning Future { List(article1) }

        for {
          article <- articleService.getArticles(request)
        } {
          article shouldBe Seq(article1)
        }
      }
    }

    "createArticle" should {
      "create an article and return the one" in new Context {
        val newArticle = Article(0, "slug", "title", "description", "body", 1, currentWhenInserting, currentWhenInserting)
        val newPostArticle = ArticlePosted("title", "description", "body", Seq())
        var request = ArticleRequest(tag = None, authorName = Some("testAuthor"), favorited = None)
        (articleStorage.createArticle _).expects(*) returning Future { newArticle }

        for {
          article <- articleService.createArticle(0, newPostArticle, Option(1))
        } {
          article shouldBe Some(newPostArticle)
        }
      }
    }

    "getArticleBySlug" should {
      "create an article by specific slug" in new Context {
        val targetArticle = Article(0, "slug", "title", "description", "body", 1, currentWhenInserting, currentWhenInserting)
        (articleStorage.getArticleBySlug _).expects("sample-slug") returning Future(Some(targetArticle))

        for {
          article <- articleService.getArticleBySlug("sample-slug")
        } {
          article shouldBe Some(targetArticle)
        }
      }
    }
  }

  trait Context {
    val articleStorage = mock[ArticleStorage]
    val userStorage = mock[UserStorage]
    val articleService = new ArticleService(articleStorage, userStorage)
  }
}
