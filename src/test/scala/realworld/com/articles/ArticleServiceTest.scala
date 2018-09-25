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
        (articleStorage.getArticles _).expects(request) returning Future{List(article1)}

        for{
          article <- articleService.getArticles(request)
        } {
          article shouldBe Seq(article1)
        }
      }
    }

    "createArticle" should {
      "create an article and return the one" in new Context {
        val newArticle = Article(0, "slug", "title", "description", "body", 1, currentWhenInserting, currentWhenInserting)
        val newPostArticle = ArticlePosted("title", "description", "body")
        var request = ArticleRequest(tag = None, authorName = Some("testAuthor"), favorited = None)
        (articleStorage.createArticle _).expects(*) returning Future{newArticle}

        for{
          article <- articleService.createArticle(0, newPostArticle)
        } {
          article shouldBe newArticle
        }
      }
    }
  }

  trait Context {
    val articleStorage = mock[ArticleStorage]
    val articleService = new ArticleService(articleStorage)
  }
}
