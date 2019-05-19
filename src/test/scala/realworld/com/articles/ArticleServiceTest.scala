package realworld.com.articles

import org.scalamock.scalatest.MockFactory
import realworld.com.BaseServiceTest
import realworld.com.core.User
import realworld.com.profile.Profile
import realworld.com.users.UserStorage

import scala.concurrent.Future

class ArticleServiceTest extends BaseServiceTest with MockFactory {
  val normalArticle = Article(
    0,
    "slug",
    "title",
    "description",
    "body",
    1,
    currentWhenInserting,
    currentWhenInserting
  )
  val normalAuthor = User(1, "author", "password", "email", None, image = None, createdAt = currentWhenInserting, updatedAt = currentWhenInserting)

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
          normalArticle
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
            2,
            currentWhenInserting,
            currentWhenInserting
          )
        )
        (articleStorage.getArticlesByFollowees _)
          .expects(*, *, *) returning Future { articles }
        (articleStorage.isFavoriteArticleIds _).expects(*, *) returning Future {
          Seq(1L, 2L)
        }
        (articleStorage.countFavorites _).expects(*) returning Future {
          Seq((1L, 0))
        }
        (userStorage.getUsersByUserIds _).expects(*) returning Future {
          Seq(normalAuthor)
        }

        for {
          article <- articleService.getFeeds(1, None, None)
        } {
          article.articlesCount shouldBe 2
          article.articles.head.title shouldBe "title"
          article.articles.head.favorited shouldBe false
        }
      }
    }

    "getArticleBySlug" should {
      "create an article by specific slug" in new Context {
        (articleStorage.getArticleBySlug _)
          .expects("sample-slug") returning Future(Some(normalArticle))

        for {
          article <- articleService.getArticleBySlug("sample-slug", 1)
        } {
          article shouldBe Some(normalArticle)
        }
      }
    }
    "updateArticleBySlug" should {
      "should update article" in new Context {
        val updateTitle = "title-test"
        val sampleSlug = "sample-slug"
        val articleUpdated = ArticleUpdated(Some(updateTitle), None, None)
        (articleStorage.getArticleBySlug _)
          .expects(sampleSlug) returning Future { Some(normalArticle) }
        (articleStorage.updateArticle _)
          .expects(
            normalArticle.copy(
              title = updateTitle,
              slug = slugify(updateTitle)
            )
          ) returning Future(
              normalArticle.copy(title = updateTitle)
            )
        for {
          article <- articleService.updateArticleBySlug(
            sampleSlug,
            0,
            articleUpdated
          )
        } {
          article.isDefined shouldBe true
          article foreach { a =>
            a.article.title shouldBe updateTitle
            a.article.slug shouldBe normalArticle.slug
            a.article.description shouldBe normalArticle.description
            a.article.body shouldBe normalArticle.body
          }
        }

      }
    }
    "deleteArticleBySlug" should {
      "should delete an article" in new Context {
        val slug = "dragon-dragon"
        (articleStorage.deleteArticleBySlug _)
          .expects(slug) returning Future { () }

        for {
          f <- articleService.deleteArticleBySlug(slug)
        } yield {
          f shouldBe a[Unit]
        }

      }
    }
    "favoriteArticle" should {
      "should favorite an article" in new Context {
        //        (articleStorage.updateArticle _)
        //          .expects(
        //            normalArticle.copy(
        //              title = updateTitle,
        //              slug = slugify(updateTitle)
        //            )
        //          ) returning Future(
        //          normalArticle.copy(title = updateTitle)
        //        )
      }
    }
  }

  trait Context {
    val articleStorage = mock[ArticleStorage]
    val userStorage = mock[UserStorage]
    val articleService = new ArticleService(articleStorage, userStorage)
  }
}
