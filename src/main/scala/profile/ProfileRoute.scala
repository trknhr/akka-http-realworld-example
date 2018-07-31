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
      get{
        complete(getProfile(username).map{
          case Some(user) =>
            OK -> user.asJson
          case None =>
            BadRequest -> None.asJson
        })
      }
      //      pathEndOrSingleSlash {
      //        authenticate(secretKey) { userId =>
      //          get {
      //            complete(getCurrentUser(userId).map {
      //              case Some(user) =>
      //                OK -> UserProfile(user.email, user.username, user.bio, user.image).asJson
      //              case None =>
      //                BadRequest -> None.asJson
      //            })
      //          }
      //        }
      //      }
    }
  }

}

//private case class LoginPasswordUser(user: LoginPassword)
//
//private case class LoginPassword(email: String, password: String)
//private case class UserProfile(username: String, email: String, bio: Option[String], image: Option[String])
//
//private case class UserUpdateParam(user: core.UserUpdate)
