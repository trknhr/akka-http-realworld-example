package realworld.com.users

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import realworld.com.core
import realworld.com.core.UserRegistration

import scala.concurrent.ExecutionContext

class UserRoute (
  secretKey: String,
  usersService: UserService
)(implicit executionContext: ExecutionContext) extends FailFastCirceSupport {

  import realworld.com.converter.Formatter._
  import realworld.com.utils.JwtAuthDirectives._
  import StatusCodes._
  import usersService._

  val route = pathPrefix("users") {
      path("login") {
        pathEndOrSingleSlash {
          post {
            entity(as[LoginPasswordUser]) { idPass =>
              complete(
                login(idPass.user.email, idPass.user.password).map {
                  case Some(user) => OK -> user.asJson
                  case None => BadRequest -> None.asJson
                }
              )

            }
          }
        }
      } ~
      pathEndOrSingleSlash {
        get {
          complete(getUsers().map(_.asJson))
        }
        post {
          entity(as[UserRegistration]) { userRegisrtration =>
            complete(register(userRegisrtration).map { user =>
              user.asJson
          })
        }
      }
    }
  } ~
  path("user") {
    pathEndOrSingleSlash {
      authenticate(secretKey) { userId =>
        get {
          complete(getCurrentUser(userId).map {
            case Some(user) =>
              OK -> UserProfile(user.email, user.username, user.bio, user.image).asJson
            case None =>
              BadRequest -> None.asJson
          })
        }
        put {
          entity(as[UserUpdateParam]) { update =>
            complete(updateUser(userId, update.user).map {
              case Some(user) =>
                OK -> UserProfile(user.username, user.email, user.bio, user.image).asJson
              case None =>
                BadRequest -> None.asJson
            })
          }
        }
      }
    }
  }
}

private case class LoginPasswordUser(user: LoginPassword)

private case class LoginPassword(email: String, password: String)
private case class UserProfile(username: String, email: String, bio: Option[String], image: Option[String])

private case class UserUpdateParam(user: core.UserUpdate)
