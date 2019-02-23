package realworld.com.utils

import org.flywaydb.core.Flyway

/**
 * Created by kunihiro on 2018/08/02.
 */
class DatabaseMigrationManager(
    jdbcUrl: String,
    dbUser: String,
    dbPassword: String
) {
  private val flyway = new Flyway()
  flyway.setDataSource(jdbcUrl, dbUser, dbPassword)

  def migrateDatabaseSchema(): Unit = flyway.migrate()

  def dropDatabase(): Unit = flyway.clean()

}
