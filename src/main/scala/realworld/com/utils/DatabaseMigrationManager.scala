package realworld.com.utils

import org.flywaydb.core.Flyway

class DatabaseMigrationManager(
  jdbcUrl: String,
  dbUser: String,
  dbPassword: String) {
  val flyway = Flyway.configure().dataSource(jdbcUrl, dbUser, dbPassword).load()

  def migrateDatabaseSchema(): Unit = flyway.migrate()

  def dropDatabase(): Unit = flyway.clean()

}
