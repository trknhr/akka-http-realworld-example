package realworld.com.articles

import org.scalamock.scalatest.MockFactory
import realworld.com.BaseServiceTest
import realworld.com.tags.TagStorage
import realworld.com.test_helpers.{ Articles, Authors }
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
          Articles.normalArticle
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
          .expects(1, None, None) returning Future { articles }
        (articleStorage.isFavoriteArticleIds _).expects(*, *) returning Future {
          Seq(1L, 2L)
        }
        (articleStorage.countFavorites _)
          .expects(*)
          .returning(Future {
            Seq((1L, 0))
          })
        (userStorage.getUsersByUserIds _).expects(*) returning Future {
          Seq(Authors.normalAuthor)
        }
        (tagStorage.getTagsByArticles _).expects(*) returning Future {
          Seq((0, TagV(1, "tag first")), (1, TagV(1, "tag second")))
        }

        whenReady(
          for {
            article <- articleService.getFeeds(1, None, None)
          } yield article
        ) { article =>
            article.articlesCount shouldBe 2
            article.articles.head.title shouldBe "title"
            article.articles.head.favorited shouldBe false
            article.articles.head.tagList shouldBe Seq("tag first")
            article.articles(1).tagList shouldBe Seq("tag second")
          }
      }
    }

    "getArticleBySlug" should {
      "create an article by specific slug" in new Context {
        (articleStorage.getArticleBySlug _)
          .expects("sample-slug") returning Future(Some(Articles.normalArticle))

        for {
          article <- articleService.getArticleBySlug("sample-slug", 1)
        } {
          article shouldBe Some(Articles.normalArticle)
        }
      }
    }
    "updateArticleBySlug" should {
      "should update article" in new Context {
        val updateTitle = "title-test"
        val sampleSlug = "sample-slug"
        val articleUpdated = ArticleUpdated(Some(updateTitle), None, None)
        (articleStorage.getArticleBySlug _)
          .expects(sampleSlug) returning Future { Some(Articles.normalArticle) }
        (articleStorage.updateArticle _)
          .expects(
            Articles.normalArticle.copy(
              title = updateTitle,
              slug = slugify(updateTitle)
            )
          ) returning Future(
              Articles.normalArticle.copy(title = updateTitle)
            )
        (articleStorage.favoriteArticle _).expects(*, *) returning Future {
          Favorite(0, 1L, 1L)
        }
        (articleStorage.countFavorite _)
          .expects(*)
          .returning(Future { 1 })
        (userStorage.getUser _).expects(*) returning Future {
          Some(Authors.normalAuthor)
        }
        (tagStorage.getTagsByArticle _).expects(*) returning Future {
          Seq(TagV(1, "first"))
        }

        whenReady(
          for {
            article <- articleService.updateArticleBySlug(
              sampleSlug,
              1,
              articleUpdated
            )
          } yield article
        ) { article =>
            article.isDefined shouldBe true
            article foreach { a =>
              a.article.title shouldBe updateTitle
              a.article.slug shouldBe Articles.normalArticle.slug
              a.article.description shouldBe Articles.normalArticle.description
              a.article.body shouldBe Articles.normalArticle.body
              a.article.tagList shouldBe Seq("first")
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
        val slug = "dragon-dragon"

        (articleStorage.getArticleBySlug _)
          .expects(slug) returning Future(Some(Articles.normalArticle))

        (articleStorage.countFavorite _)
          .expects(*)
          .returning(Future { 1 })

        (articleStorage.favoriteArticle _).expects(*, *) returning Future {
          Favorite(0, 0, 0)
        }
        (userStorage.getUser _).expects(*) returning Future {
          Some(Authors.normalAuthor)
        }
        (tagStorage.getTagsByArticle _).expects(*) returning Future {
          Seq(TagV(1, "first"))
        }

        whenReady(
          for (
            a <- articleService.favoriteArticle(0, slug)
          ) yield a
        ) { oa =>
            oa.map { a =>
              a.article.title shouldBe Articles.normalArticle.title
              a.article.slug shouldBe Articles.normalArticle.slug
              a.article.description shouldBe Articles.normalArticle.description
              a.article.body shouldBe Articles.normalArticle.body
              a.article.favorited shouldBe true
              a.article.favoritesCount shouldBe 2
              a.article.tagList shouldBe Seq("first")
            }
          }
      }
    }

    "unFavoriteArticle" should {
      "should unfavorite an article" in new Context {
        val slug = "dragon-dragon"

        (articleStorage.getArticleBySlug _)
          .expects(slug) returning Future(Some(Articles.normalArticle))

        (articleStorage.countFavorite _)
          .expects(*)
          .returning(Future { 0 })

        (articleStorage.unFavoriteArticle _).expects(*, *) returning Future {
          1
        }

        (userStorage.getUser _).expects(*) returning Future {
          Some(Authors.normalAuthor)
        }

        (tagStorage.getTagsByArticle _).expects(*) returning Future {
          Seq(TagV(1, "first"))
        }

        whenReady(
          for (
            a <- articleService.unFavoriteArticle(0, slug)
          ) yield a
        ) { oa =>
            println(oa)
            oa.map { a =>
              a.article.title shouldBe Articles.normalArticle.title
              a.article.slug shouldBe Articles.normalArticle.slug
              a.article.description shouldBe Articles.normalArticle.description
              a.article.body shouldBe Articles.normalArticle.body
              a.article.favorited shouldBe false
              a.article.favoritesCount shouldBe 0
              a.article.tagList shouldBe Seq("first")
            }
          }
      }
    }
  }

  trait Context {
    val articleStorage = mock[ArticleStorage]
    val userStorage = mock[UserStorage]
    val tagStorage = mock[TagStorage]
    val articleService = new ArticleService(articleStorage, userStorage, tagStorage)
  }
}
