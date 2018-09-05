package realworld.com.articles

import realworld.com.utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

trait ArticleStorage{
  def getArticles(/*tag: Option[String], author: Option[String], limit: Int = 20, offset: Int = 0): Future[Seq[Article]*/]
}
class JdbcArticleStorage(
                       val databaseConnector: DatabaseConnector
                     )(implicit executionContext: ExecutionContext)
  extends ArticleTable{
  import databaseConnector._
  import databaseConnector.profile.api._

  def getArticles(/*tag: Option[String], author: Option[String], limit: Int = 20, offset: Int = 0*/) =
      db.run(articles.result)
//    db.run(articles.filter
//      m => m.id === 0l).result)

}
