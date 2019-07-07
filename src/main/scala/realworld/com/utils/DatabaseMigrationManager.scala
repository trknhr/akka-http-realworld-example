package realworld.com.utils

import org.flywaydb.core.Flyway

class DatabaseMigrationManager(
  jdbcUrl: String,
  dbUser: String,
  dbPassword: String) {
  private val flyway = new Flyway()
  flyway.setDataSource(jdbcUrl, dbUser, dbPassword)

  def migrateDatabaseSchema(): Unit = flyway.migrate()

  def dropDatabase(): Unit = flyway.clean()

}
