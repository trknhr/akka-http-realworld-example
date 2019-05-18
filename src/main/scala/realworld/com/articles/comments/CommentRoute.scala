package realworld.com.articles.comments

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import realworld.com.articles.{ ArticleRoute, ArticleService, comments }
import realworld.com.profile.{ ProfileRoute, ProfileService }
import realworld.com.users.{ UserRoute, UserService }
import io.circe.generic.auto._
import io.circe.syntax._
import akka.http.scaladsl.model.StatusCodes

import scala.concurrent.ExecutionContext

class CommentRoute(
  secretKey: String,
  commentService: CommentService
)(implicit executionContext: ExecutionContext)
    extends FailFastCirceSupport {

  import akka.http.scaladsl.model.StatusCodes._
  import realworld.com.converter.Formatter._
  import realworld.com.utils.JwtAuthDirectives._
  import commentService._

  val route = authenticate(secretKey) { userId =>
    path(Segment / "comments") { slug =>
      pathEndOrSingleSlash {
        get {
          complete(getComments(slug, userId))
        } ~
          post {
            entity(as[CommentRequest]) { comment =>
              complete(createComment(slug, userId, comment).map {
                case Some(x) => OK -> x.asJson
                case None => NotFound -> None.asJson
              })
            }
          }
      }
    }
  } ~ path(Segment / "comments" / IntNumber) { (slug: String, commentId: Int) =>
    pathEndOrSingleSlash {
      delete {
        complete(deleteComment(slug, commentId))
      }
    }
  }
}

//case class CommentRequest(comment: CommentForJson)
////private case class CreateComment(comment: CommentForJson)
//private case class CommentForJson(body: String)
//private case class CreateArticle(article: ArticlePosted)
//case class FeedRequest(
//                        limit: Option[Long] = Some(100),
//                        offset: Option[Long] = Some(0)
//                      )
