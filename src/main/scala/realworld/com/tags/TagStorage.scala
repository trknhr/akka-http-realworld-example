package realworld.com.tags

import realworld.com.articles.{ArticleTagTable, TagTable, TagV}
import realworld.com.utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

trait TagStorage {
  def getTags(): Future[Seq[TagV]]
  def getTagsByArticles(articleIds: Seq[Long]): Future[Seq[(Long, TagV)]]
  def getTagsByArticle(articleId: Long): Future[Seq[TagV]]
  def findTagByNames(tagNames: Seq[String]): Future[Seq[TagV]]
  def insertAndGet(tagVs: Seq[TagV]): Future[Seq[TagV]]
}
class JdbcTagStorage(
  val databaseConnector: DatabaseConnector
)(implicit executionContext: ExecutionContext)
    extends TagStorage
    with TagTable with ArticleTagTable {
  import databaseConnector._
  import databaseConnector.profile.api._

  def getTags(): Future[Seq[TagV]] =
    db.run(tags.result)

  def getTagsByArticles(articleIds: Seq[Long]): Future[Seq[(Long, TagV)]] =
    db.run(
      articleTags.join(tags).on(_.tagId === _.id).filter(_._1.articleId inSet articleIds).map(a => (a._1.articleId, a._2)).result
    )

  def getTagsByArticle(articleId: Long): Future[Seq[TagV]] =
    db.run(
      articleTags.join(tags).on(_.tagId === _.id).filter(_._1.articleId === articleId).map(_._2).result
    )

  def findTagByNames(tagNames: Seq[String]): Future[Seq[TagV]] =
    db.run(
      tags.filter(_.name inSet tagNames).result
    )

  def insertAndGet(tagVs: Seq[TagV]): Future[Seq[TagV]] = {
    val articlesIds =
      tags
        .returning(tags.map(_.id))
        .++=(tagVs)
        .flatMap(ids => tags.filter(_.id inSet ids).result)

    db.run(articlesIds)
  }
}
