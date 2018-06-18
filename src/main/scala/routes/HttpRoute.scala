package routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import users.{UserRoute, UserService}

import scala.concurrent.ExecutionContext

/**
  * Created by kunihiro on 2018/06/13.
  */
class HttpRoute (
  userService: UserService,
  secretKey: String
) (implicit executionContext: ExecutionContext) {
//  private val userRouter = new UserRoute(secretKey, userService)
  private val usersRouter = new UserRoute(secretKey, userService)

  val route: Route =
    cors(){
      usersRouter.route ~
      pathPrefix("healthcheck"){
        get {
          complete("OK")
        }
      }
    }
}
