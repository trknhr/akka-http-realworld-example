package realworld.com.tags

import realworld.com.profile.ProfileService
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import realworld.com.articles.comments.CommentRoute

import scala.concurrent.ExecutionContext

class TagRoute(
  secretKey: String,
  tagService: TagService
)(implicit executionContext: ExecutionContext)
    extends FailFastCirceSupport {

  import akka.http.scaladsl.model.StatusCodes._
  import tagService._
  import realworld.com.utils.JwtAuthDirectives._

  val route = pathPrefix("tags") {
    pathEnd {
      get {
        complete(getTags().map(_.asJson))
      }
    }
  }
}

