package realworld.com

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import profile.ProfileService
import realworld.com.articles.comments.{ CommentService, JdbcCommentStorage }
import realworld.com.articles.{ ArticleService, JdbcArticleStorage }
import realworld.com.routes.routes.HttpRoute
import realworld.com.tags.{ JdbcTagStorage, TagService }
import users.{ JdbcUserStorage, UserService }
import utils.{ Config, DatabaseConnector, DatabaseMigrationManager, StorageRunner }

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext

object Main extends App {
  def startApplication() = {
    implicit val system: ActorSystem = ActorSystem("real-world-akka-http")
    implicit val executor: ExecutionContext = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val config = Config.load()

    val databaseConnector = new DatabaseConnector(
      config.database.jdbcUrl,
      config.database.username,
      config.database.password)

    val flywayService = new DatabaseMigrationManager(
      config.database.jdbcUrl,
      config.database.username,
      config.database.password)
    flywayService.migrateDatabaseSchema()

    val userStorage = new JdbcUserStorage()

    val articleStorage = new JdbcArticleStorage()

    val commentStorage = new JdbcCommentStorage()

    val tagStorage = new JdbcTagStorage()

    val storageRunner = new StorageRunner(databaseConnector)

    val userService = new UserService(storageRunner, userStorage, config.secretKey)

    val profileService = new ProfileService(storageRunner, userStorage)

    val articleService = new ArticleService(storageRunner, articleStorage, userStorage, tagStorage)

    val commentService = new CommentService(storageRunner, articleStorage, commentStorage, userStorage)

    val tagService = new TagService(storageRunner, tagStorage)

    val httpRoute = new HttpRoute(
      userService,
      profileService,
      articleService,
      commentService,
      tagService,
      config.secretKey)

    Http().bindAndHandle(httpRoute.route, config.http.host, config.http.port)

    println(s"Working at http://${config.http.host}:${config.http.port}/")

    Await.result(system.whenTerminated, Duration.Inf)
  }

  startApplication()
}
