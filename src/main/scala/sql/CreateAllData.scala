package sql

import java.sql.Timestamp
import java.util.Date

import realworld.com.core.User
import realworld.com.users.{JdbcUserStorage, UserStorage}
import realworld.com.utils.{Config, DatabaseConnector, DatabaseMigrationManager}

import scala.concurrent.ExecutionContext

object CreateAllData {
  def currentWhenInserting = new Timestamp((new Date).getTime)
  def seeds(implicit executionContext: ExecutionContext) = {
    val config = Config.load()

    val databaseConnector = new DatabaseConnector(
      config.database.jdbcUrl,
      config.database.username,
      config.database.password
    )

    val flywayService = new DatabaseMigrationManager(
      config.database.jdbcUrl,
      config.database.username,
      config.database.password
    )

    flywayService.dropDatabase()
    flywayService.migrateDatabaseSchema()
    val userStorage: UserStorage = new JdbcUserStorage(databaseConnector)
    UserSeed.data.foreach(userStorage.saveUser(_))
  }
}
