package realworld.com.routes.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import realworld.com.articles.{ArticleRoute, ArticleService}
import realworld.com.profile.{ProfileRoute, ProfileService}
import realworld.com.users.{UserRoute, UserService}

import scala.concurrent.ExecutionContext

/**
  * Created by kunihiro on 2018/06/13.
  */
class HttpRoute(
    userService: UserService,
    profileService: ProfileService,
    articleService: ArticleService,
    secretKey: String
)(implicit executionContext: ExecutionContext) {
  private val usersRouter = new UserRoute(secretKey, userService)
  private val profileRouter = new ProfileRoute(secretKey, profileService)
  private val articleRouter = new ArticleRoute(secretKey, articleService)

  val route: Route =
    cors() {
      usersRouter.route ~
        profileRouter.route ~
        articleRouter.route ~
        pathPrefix("healthcheck") {
          get {
            complete("OK")
          }
        }
    }
}
