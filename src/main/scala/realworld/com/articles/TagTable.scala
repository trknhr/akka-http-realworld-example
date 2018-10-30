package realworld.com.articles

import java.sql.Timestamp
import java.util.Date

import realworld.com.utils.DatabaseConnector

trait TagTable {
  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class Tags(tag: Tag) extends Table[TagV](tag, "tags") {
    def currentWhenInserting = new Timestamp((new Date).getTime)
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def name = column[String]("name")

    def * =
      (id, name) <> ((TagV.apply _).tupled, TagV.unapply)
  }

  protected val tags = TableQuery[Tags]
}
