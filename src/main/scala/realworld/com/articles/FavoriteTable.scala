package realworld.com.articles

import java.sql.Timestamp
import java.util.Date

import realworld.com.utils.DatabaseConnector

trait FavoriteTable {
  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class Favorites(tag: Tag) extends Table[Favorite](tag, "favorite") {
    def currentWhenInserting = new Timestamp((new Date).getTime)
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def userId = column[Long]("user_id")
    def favoritedId = column[Long]("favorited_id")

    def * =
      (id, userId, favoritedId) <> ((Favorite.apply _).tupled, Favorite.unapply)
  }

  protected val favorites = TableQuery[Favorites]
}
