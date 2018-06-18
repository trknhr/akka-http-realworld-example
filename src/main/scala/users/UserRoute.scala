package users

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.ExecutionContext

class UserRoute (
  secretKey: String,
  usersService: UserService
)(implicit executionContext: ExecutionContext) extends FailFastCirceSupport {

  import common.converter.Formatter._
  import StatusCodes._
  import usersService._

  val route = pathPrefix("users") {
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
  }
//    pathPrefix(Segment) { id =>
//      pathEndOrSingleSlash {
//        get {
//          complete(getUsers)
//
//        }
//      }
//    }

}
