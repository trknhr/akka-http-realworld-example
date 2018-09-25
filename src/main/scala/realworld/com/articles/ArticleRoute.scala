package realworld.com.articles

import realworld.com.profile.ProfileService
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import scala.concurrent.ExecutionContext

class ArticleRoute(
   secretKey: String,
   articleService: ArticleService
                   )(implicit executionContext: ExecutionContext) extends FailFastCirceSupport {
  import akka.http.scaladsl.model.StatusCodes._
  import realworld.com.converter.Formatter._
  import articleService._
  import realworld.com.utils.JwtAuthDirectives._

  val route = pathPrefix("articles") {
      pathEndOrSingleSlash {
        get {
          entity(as[ArticleRequest]) {
            request =>
            complete(getArticles(request).map(_.asJson))
          }
        }
      }~
      post {
        authenticate(secretKey) { authorId =>
          entity(as[CreateArticle]) { article =>
            complete(createArticle(authorId, article.article).map { article =>
              article.asJson
            })
          }
        }
      }
  }
}
private case class CreateArticle(article: ArticlePosted)

