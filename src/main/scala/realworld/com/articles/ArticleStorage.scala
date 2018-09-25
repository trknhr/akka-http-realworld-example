package realworld.com.articles

import realworld.com.users.UserProfileTable
import realworld.com.utils.DatabaseConnector
import slick.lifted.CanBeQueryCondition

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

trait ArticleStorage{
  def getArticles(pageRequest: ArticleRequest): Future[Seq[Article]]
  def createArticle(newArticle: Article): Future[Article]
}
class JdbcArticleStorage(
             val databaseConnector: DatabaseConnector
           )(implicit executionContext: ExecutionContext)
  extends ArticleStorage with ArticleTable with UserProfileTable{
  import databaseConnector._
  import databaseConnector.profile.api._

  def getArticles(pageRequest: ArticleRequest): Future[Seq[Article]] = {
    val joins = articles.join(users).on(_.authorId === _.id)

    db.run(
      MaybeFilter(joins)
        .filter(pageRequest.authorName)(authorname => tables => tables._2.username === authorname)
        .query.map(_._1).result
    )
  }

  def createArticle(newArticle: Article): Future[Article] = {
    val articleWithId =
      (articles returning articles.map(_.id) into ((u, id) => u.copy(id = id + 1))) += newArticle

    db.run(articleWithId)
  }

  case class MaybeFilter[X, Y](query: Query[X, Y, Seq]) {
    def filter[T, R <: Rep[_] : CanBeQueryCondition](data: Option[T])(f: T => X => R): MaybeFilter[X, Y] = {
      data.map(v => MaybeFilter(query.filter(f(v)))).getOrElse(this)
    }
  }
}
