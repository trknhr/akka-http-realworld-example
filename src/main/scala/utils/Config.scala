package utils

import pureconfig.loadConfig

/**
  * Created by kunihiro on 2018/06/13.
  */
case class Config(secretKey: String, http: HttpConfig, database: DatabaseConfig)

object Config {
  def load() =
    loadConfig[Config] match {
      case Right(config) => config
      case Left(error) =>
        throw new RuntimeException("Cannot load config file" + error)
    }
}

private[utils] case class HttpConfig(host: String, port: Int)
private[utils] case class DatabaseConfig(jdbcUrl: String, username: String, password: String)