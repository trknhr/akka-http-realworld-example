package realworld.com.profile

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import scala.concurrent.ExecutionContext

class ProfileRoute (
  secretKey: String,
  profileService: ProfileService
)(implicit executionContext: ExecutionContext) extends FailFastCirceSupport {

  import StatusCodes._
  import profileService._
  import realworld.com.utils.JwtAuthDirectives._

  val route = pathPrefix("profiles") {
    authenticate(secretKey) { userId =>
      path(Segment) { username =>
        pathEndOrSingleSlash {
          get {
            complete(getProfile(userId, username).map {
              case Some(user) =>
                OK -> user.asJson
              case None =>
                BadRequest -> None.asJson
            })
          }
        }
      } ~
        path("""[0-9 a-z A-z]+""".r / "follow") { username =>
          pathEndOrSingleSlash {
            post {
              complete(follow(userId, username))
            }
          }
        }
    }
  }
}

