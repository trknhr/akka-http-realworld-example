package realworld.com.tags

import realworld.com.articles.{ TagTable, TagV }
import realworld.com.utils.DatabaseConnector

import scala.concurrent.{ ExecutionContext, Future }

trait TagStorage {
  def getTags(): Future[Seq[TagV]]
}
class JdbcTagStorage(
  val databaseConnector: DatabaseConnector
)(implicit executionContext: ExecutionContext)
    extends TagStorage
    with TagTable {
  import databaseConnector._
  import databaseConnector.profile.api._

  def getTags(): Future[Seq[TagV]] =
    db.run(tags.result)
}
