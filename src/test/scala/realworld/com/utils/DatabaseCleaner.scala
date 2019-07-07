package realworld.com.utils

import slick.jdbc.SQLActionBuilder
import slick.jdbc.SetParameter.SetUnit
import slick.jdbc.meta.{ MQName, MTable }
import scala.util.{ Failure, Success }

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

object DatabaseCleaner {

  def cleanDatabase(databaseConnector: DatabaseConnector)(
    implicit
    executionContext: ExecutionContext): Unit = {
    import databaseConnector._
    import databaseConnector.profile.api._

    val truncatesFuture = db
      .run(
        MTable.getTables)
      .map {
        _.filter {
          case MTable(tableName, "TABLE", _, _, _, _) => true
          case _ => false
        }.map {
          case MTable(tableName, _, _, _, _, _) =>
            SQLActionBuilder(List(s"TRUNCATE TABLE ${tableName.name} CASCADE"), SetUnit).asUpdate
        }
      }

    val truncates = Await.result(
      truncatesFuture, Duration.Inf)

    Await.result(db.run(
      DBIO.sequence(
        truncates)), Duration.Inf)
  }
}
