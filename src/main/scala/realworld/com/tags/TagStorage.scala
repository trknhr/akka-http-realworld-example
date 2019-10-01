package realworld.com.tags

import realworld.com.articles.{ArticleTagTable, TagTable, TagV}
import realworld.com.utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}
import slick.dbio.{DBIO, DBIOAction}

import slick.jdbc.PostgresProfile.api.{
  DBIO => _,
  MappedTo => _,
  Rep => _,
  TableQuery => _,
  _
}
import scala.concurrent.ExecutionContext.Implicits.global

trait TagStorage {
  def getTags(): DBIO[Seq[TagV]]
  def getTagsByArticles(articleIds: Seq[Long]): DBIO[Seq[(Long, TagV)]]
  def getTagsByArticle(articleId: Long): DBIO[Seq[TagV]]
  def findTagByNames(tagNames: Seq[String]): DBIO[Seq[TagV]]
  def insertAndGet(tagVs: Seq[TagV]): DBIO[Seq[TagV]]
}
class JdbcTagStorage extends TagStorage with TagTable with ArticleTagTable {

  def getTags(): DBIO[Seq[TagV]] =
    tags.result

  def getTagsByArticles(articleIds: Seq[Long]): DBIO[Seq[(Long, TagV)]] =
    articleTags
      .join(tags)
      .on(_.tagId === _.id)
      .filter(_._1.articleId inSet articleIds)
      .map(a => (a._1.articleId, a._2))
      .result

  def getTagsByArticle(articleId: Long): DBIO[Seq[TagV]] =
    articleTags
      .join(tags)
      .on(_.tagId === _.id)
      .filter(_._1.articleId === articleId)
      .map(_._2)
      .result

  def findTagByNames(tagNames: Seq[String]): DBIO[Seq[TagV]] =
    tags.filter(_.name inSet tagNames).result

  def insertAndGet(tagVs: Seq[TagV]): DBIO[Seq[TagV]] =
    tags
      .returning(tags.map(_.id))
      .++=(tagVs)
      .flatMap(ids => tags.filter(_.id inSet ids).result)

}
