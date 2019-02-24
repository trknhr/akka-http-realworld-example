package realworld.com.routes.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import realworld.com.articles.comments.{ CommentRoute, CommentService }
import realworld.com.articles.{ ArticleRoute, ArticleService }
import realworld.com.profile.{ ProfileRoute, ProfileService }
import realworld.com.tags.{ TagRoute, TagService }
import realworld.com.users.{ UserRoute, UserService }

import scala.concurrent.ExecutionContext

class HttpRoute(
    userService: UserService,
    profileService: ProfileService,
    articleService: ArticleService,
    commentService: CommentService,
    tagService: TagService,
    secretKey: String
)(implicit executionContext: ExecutionContext) {
  private val usersRouter = new UserRoute(secretKey, userService)
  private val profileRouter = new ProfileRoute(secretKey, profileService)
  private val commentRouter = new CommentRoute(secretKey, commentService)
  private val articleRouter = new ArticleRoute(commentRouter, secretKey, articleService)
  private val tagRouter = new TagRoute(secretKey, tagService)

  val route: Route =
    cors() {
      usersRouter.route ~
        profileRouter.route ~
        articleRouter.route ~
        tagRouter.route ~
        pathPrefix("healthcheck") {
          get {
            complete("OK")
          }
        }
    }
}
