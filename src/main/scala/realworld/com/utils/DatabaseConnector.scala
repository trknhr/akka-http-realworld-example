package realworld.com.utils

import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }

class DatabaseConnector(jdbcURL: String, dbUser: String, dbPassword: String) {

  private val hikariDataSource = {
    val hikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl(jdbcURL)
    hikariConfig.setUsername(dbUser)
    hikariConfig.setPassword(dbPassword)

    new HikariDataSource(hikariConfig)
  }

  val profile = slick.jdbc.PostgresProfile
  import profile.api._

  val db = Database.forDataSource(hikariDataSource, None)
  db.createSession()
}
