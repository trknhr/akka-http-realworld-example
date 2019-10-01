package realworld.com.articles

import java.sql.Timestamp
import java.util.Date

import slick.jdbc.PostgresProfile.api.{
  DBIO => _,
  MappedTo => _,
  Rep => _,
  TableQuery => _,
  _
}
import slick.lifted.TableQuery

trait TagTable {

  class Tags(tag: Tag) extends Table[TagV](tag, "tags") {
    def currentWhenInserting = new Timestamp((new Date).getTime)
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def name = column[String]("name")

    def * =
      (id, name) <> ((TagV.apply _).tupled, TagV.unapply)
  }

  protected val tags = TableQuery[Tags]
}
