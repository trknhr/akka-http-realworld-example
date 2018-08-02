package utils

import de.flapdoodle.embed.process.runtime.Network._
import ru.yandex.qatools.embed.postgresql.PostgresStarter
i
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.{Credentials, Net, Storage, Timeout}
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig
import ru.yandex.qatools.embed.postgresql.distribution.Version

/**
  * Created by kunihiro on 2018/08/02.
  */
class InMemoryPostgresStorage {
  val dbHost = getLocalHost.getHostAddress
  val dbPort = 25535
  val dbName = "database-name"
  val dbUser = "user"
  val dbPassword = "password"
  val jdbcUrl = s"jdbc:postgresql:$dbHost:$dbPort/$dbName"

  val psqlConfig = new PostgresConfig(
    Version.V9_6_3, new Net(dbHost, dbPort),
    new Storage(dbName), new Timeout(),
    new Credentials(dbUser, dbPassword)
  )

  val psqlInstance = PostgresStarter.getDefaultInstance
  val flywayService = new DataMigrationManager()

}
