package profile

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import users.{UserService, core}
import users.core.UserRegistration
import users.core._

import scala.concurrent.ExecutionContext

class ProfileRoute (
  secretKey: String,
  profileService: ProfileService
)(implicit executionContext: ExecutionContext) extends FailFastCirceSupport {

  import common.converter.Formatter._
  import utils.JwtAuthDirectives._
  import StatusCodes._
  import profileService._

  val route = pathPrefix("profiles") {
    path(Segment) { username =>
      pathEndOrSingleSlash {
        authenticate(secretKey) { userId =>
          get {
            complete(getProfile(userId, username).map {
              case Some(user) =>
                OK -> user.asJson
              case None =>
                BadRequest -> None.asJson
            })
          }
        }
      }
    }
  }

}

