package realworld.com.utils

import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}

class StorageRunner(
    val databaseConnector: DatabaseConnector
) {
  import databaseConnector._

  def run[T](actions: DBIO[T]): Future[T] = db.run(actions)

  def runInTransaction[T](action: DBIO[T]): Future[T] =
    db.run(action.transactionally)
}

