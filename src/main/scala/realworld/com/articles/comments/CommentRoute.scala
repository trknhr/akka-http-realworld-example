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

  val route = path(Segment / "comments") { slug =>
    authenticate(secretKey) { userId =>
      pathEndOrSingleSlash {
        get {
          complete("ok")
        } ~
          post {
            entity(as[CommentRequest]) { comment =>
              //              complete(createComment(slug, userId, comment).map {
              //                case Some(c) =>
              //                  OK -> c
              //                case None =>
              //                  BadRequest -> None.asJson
              //              })
              complete(createComment(slug, userId, comment).map { c =>
                c match {
                  case Some(x) => OK -> x.asJson
                  case None => NotFound -> None.asJson
                }
                //                c match {
                //                  case Some(c) => OK -> "ok"
                //                  case None => BadGateway -> None.asJson
                //                }
              })
            }
          }
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
