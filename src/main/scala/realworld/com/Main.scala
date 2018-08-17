package realworld.com

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import profile.{ProfileService}
import realworld.com.routes.routes.HttpRoute
import users.{JdbcUserStorage, UserService}
import utils.{Config, DatabaseConnector}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scala.concurrent.ExecutionContext

object Main  extends App {
  def startApplication() = {
    implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
    implicit val executor: ExecutionContext = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val config = Config.load()

    val databaseConnector = new DatabaseConnector(
      config.database.jdbcUrl,
      config.database.username,
      config.database.password
    )

    val userStorage = new JdbcUserStorage(databaseConnector)

    val userService = new UserService(userStorage, config.secretKey)

    val profileService = new ProfileService(userStorage)

    val httpRoute = new HttpRoute(userService, profileService, config.secretKey)

    Http().bindAndHandle(httpRoute.route, config.http.host, config.http.port)

    println(s"Server online at http://${config.http.host}:${config.http.port}/")

    Await.result(system.whenTerminated, Duration.Inf)
  }

  startApplication()
}
