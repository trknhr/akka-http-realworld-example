package realworld.com.articles

import org.scalamock.scalatest.MockFactory
import realworld.com.BaseServiceTest
import realworld.com.core.User
import realworld.com.profile.Profile
import realworld.com.users.UserStorage

import scala.concurrent.Future

class ArticleServiceTest extends BaseServiceTest with MockFactory {
  "ArticleService" when {
    "getArticles" should {
      "return articles by username" in new Context {
        val article1 = Article(
          0,
          "slug",
          "title",
          "description",
          "body",
          1,
          currentWhenInserting,
          currentWhenInserting
        )
        var request = ArticleRequest(
          tag = None,
          authorName = Some("testAuthor"),
          favorited = None,
          limit = None,
          offset = None
        )
        (articleStorage.getArticles _).expects(request) returning Future {
          List(article1)
        }

        for {
          article <- articleService.getArticles(request)
        } {
          article shouldBe Seq(article1)
        }
      }
    }

    "createArticle" should {
      "create an article and return the one" in new Context {
        val newArticle = Article(
          0,
          "slug",
          "title",
          "description",
          "body",
          1,
          currentWhenInserting,
          currentWhenInserting
        )
        val newPostArticle =
          ArticlePosted("title", "description", "body", Seq())
        var request = ArticleRequest(
          tag = None,
          authorName = Some("testAuthor"),
          favorited = None,
          limit = None,
          offset = None
        )
        (articleStorage.createArticle _).expects(*) returning Future {
          newArticle
        }

        for {
          article <- articleService.createArticle(0, newPostArticle, Option(1))
        } {
          article shouldBe Some(newPostArticle)
        }
      }
    }

    "getFeeds" should {
      "create an article and return the one" in new Context {
        val articles = Seq(
          Article(
            0,
            "slug",
            "title",
            "description",
            "body",
            1,
            currentWhenInserting,
            currentWhenInserting
          ),
          Article(
            1,
            "slug-2",
            "title-2",
            "description-2",
            "body-2",
            1,
            currentWhenInserting,
            currentWhenInserting
          )
        )
        (articleStorage.getArticlesByFollowees _)
          .expects(1, None, None) returning Future { articles }
        (articleStorage.isFavoriteArticleIds _).expects(*, *) returning Future {
          Seq()
        }
        (articleStorage.countFavorites _).expects(*) returning Future {
          Seq((2L, 0))
        }

        for {
          article <- articleService.getFeeds(1, None, None)
        } {
          article.length shouldBe 2
          article.head.title shouldBe "title"
          article.head.favorited shouldBe false
        }
      }
    }

    "getArticleBySlug" should {
      "create an article by specific slug" in new Context {
        val targetArticle = Article(
          0,
          "slug",
          "title",
          "description",
          "body",
          1,
          currentWhenInserting,
          currentWhenInserting
        )
        (articleStorage.getArticleBySlug _)
          .expects("sample-slug") returning Future(Some(targetArticle))

        for {
          article <- articleService.getArticleBySlug("sample-slug")
        } {
          article shouldBe Some(targetArticle)
        }
      }
    }
    "updateArticleBySlug" should {
      "should update article" in new Context {
        val updateTitle = "title-test"
        val sampleSlug = "sample-slug"
        val articleUpdated = ArticleUpdated(Some(updateTitle), None, None)
        val articleExpect = Article(
          0,
          "slug",
          "title",
          "description",
          "body",
          1,
          currentWhenInserting,
          currentWhenInserting
        )
        (articleStorage.getArticleBySlug _)
          .expects(sampleSlug) returning Future { Some(articleExpect) }
        (articleStorage.updateArticle _)
          .expects(
            articleExpect.copy(
              title = updateTitle,
              slug = slugify(updateTitle)
            )
          ) returning Future(
              articleExpect.copy(title = updateTitle)
            )
        for {
          article <- articleService.updateArticleBySlug(
            sampleSlug,
            articleUpdated
          )
        } {
          article.isDefined shouldBe true
          article foreach { a =>
            a.title shouldBe updateTitle
            a.slug shouldBe articleExpect.slug
            a.description shouldBe articleExpect.description
            a.body shouldBe articleExpect.body
          }
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
