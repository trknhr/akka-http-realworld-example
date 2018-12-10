package realworld.com.articles

import realworld.com.utils.DatabaseConnector

trait ArticleTagTable {
  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class ArticleTags(tag: Tag) extends Table[ArticleTag](tag, "articles_tags") {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def articleId = column[Long]("article_id")
    def tagId = column[Long]("tag_id")

    def * =
      (id, articleId, tagId) <> ((ArticleTag.apply _).tupled, ArticleTag.unapply)
  }

  protected val articleTags = TableQuery[ArticleTags]
}
