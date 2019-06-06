package realworld.com.articles

import realworld.com.profile.ProfileService
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import realworld.com.articles.comments.CommentRoute

import scala.concurrent.ExecutionContext

class ArticleRoute(
  commentRoute: CommentRoute,
  secretKey: String,
  articleService: ArticleService
)(implicit executionContext: ExecutionContext)
    extends FailFastCirceSupport {
  import akka.http.scaladsl.model.StatusCodes._
  import articleService._
  import realworld.com.utils.JwtAuthDirectives._

  val route = pathPrefix("articles") {
    pathEnd {
      get {
        parameters('tag.?, 'authorName.?, 'favorited.?, 'limit.as[Long].?, 'offset.as[Long].?).as(ArticleRequest) { request =>
          complete(getArticles(request).map(_.asJson))
        }
      } ~
        post {
          authenticate(secretKey) { authorId =>
            entity(as[CreateArticle]) { article =>
              complete(
                createArticle(authorId, article.article, Some(authorId)).map {
                  article =>
                    article.asJson
                }
              )
            }
          }
        }
    } ~
      path("feed") {
        pathEndOrSingleSlash {
          get {
            authenticate(secretKey) { userId =>
              parameters("limit".as[Int].?, "offfset".as[Int].?) {
                (limit, offset) =>
                  complete(getFeeds(userId, limit, offset).map(_.asJson))
              }
            }
          }
        }
      } ~
      path(Segment) { slug =>
        authenticate(secretKey) { userId =>
          pathEndOrSingleSlash {
            get {
              complete(getArticleBySlug(slug, userId))
            } ~
              put {
                entity(as[UpdateArticle]) { updateArticle =>
                  complete(updateArticleBySlug(slug, userId, updateArticle.article))
                }
              } ~
              delete {
                complete(deleteArticleBySlug(slug))
              }
          }
        }
      } ~
      path(Segment / "favorite") { slug =>
        authenticate(secretKey) { userId =>
          post {
            complete(favoriteArticle(userId, slug).map {
              case Some(x) => OK -> x.asJson
              case None => NotFound -> None.asJson
            })
          } ~
            delete {
              complete(unFavoriteArticle(userId, slug).map {
                case Some(x) => OK -> x.asJson
                case None => NotFound -> None.asJson
              })
            }
        }
      } ~
      commentRoute.route
  }
}

private case class UpdateArticle(article: ArticleUpdated)
private case class CreateArticle(article: ArticlePosted)
case class FeedRequest(
  limit: Option[Long] = Some(100),
  offset: Option[Long] = Some(0)
)
