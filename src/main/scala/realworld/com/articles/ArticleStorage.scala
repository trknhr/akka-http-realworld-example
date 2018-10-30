package realworld.com.articles

import realworld.com.profile.UserFollowersTable
import realworld.com.users.UserProfileTable
import realworld.com.utils.DatabaseConnector
import slick.lifted.CanBeQueryCondition

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

trait ArticleStorage {
  def getArticles(pageRequest: ArticleRequest): Future[Seq[Article]]
  def getArticlesByFollowees(userId: Long,
                             limit: Option[Int],
                             offset: Option[Int]): Future[Seq[Article]]
  def createArticle(newArticle: Article): Future[Article]
  def getArticleBySlug(slug: String): Future[Option[Article]]
  def isFavoriteArticleIds(userId: Long,
                           articleIds: Seq[Long]): Future[Seq[Long]]
  def countFavorites(articleIds: Seq[Long]): Future[Seq[(Long, Int)]]
  def findTagByNames(tagNames: Seq[String]): Future[Seq[TagV]]
  def insertAndGet(tagVs: Seq[TagV]): Future[Seq[TagV]]
}

class JdbcArticleStorage(
    val databaseConnector: DatabaseConnector
)(implicit executionContext: ExecutionContext)
    extends ArticleStorage
    with ArticleTable
    with UserProfileTable
    with UserFollowersTable
    with TagTable
    with ArticleTagTable
    with FavoriteTable {
  import databaseConnector._
  import databaseConnector.profile.api._

  def getArticles(pageRequest: ArticleRequest): Future[Seq[Article]] = {
    val joins = articles.join(users).on(_.authorId === _.id)

    db.run(
      MaybeFilter(joins)
        .filter(pageRequest.authorName)(authorname =>
          tables => tables._2.username === authorname)
        .query
        .map(_._1)
        .result
    )
  }

  def getArticlesByFollowees(userId: Long,
                             limit: Option[Int],
                             offset: Option[Int]): Future[Seq[Article]] =
    db.run(
      followers
        .join(articles)
        .on(_.followeeId === _.id)
        .filter(a => a._1.userId === userId)
        .drop(offset.getOrElse(0))
        .take(limit.getOrElse(1000))
        .map(_._2)
        .result
    )

  def createArticle(newArticle: Article): Future[Article] = {
    val articleWithId =
      (articles returning articles.map(_.id) into (
          (u,
           id) => u.copy(id = id + 1))) += newArticle

    db.run(articleWithId)
  }

  def getArticleBySlug(slug: String): Future[Option[Article]] =
    db.run(articles.filter(_.slug === slug).result.headOption)

  def isFavoriteArticleIds(userId: Long,
                           articleIds: Seq[Long]): Future[Seq[Long]] =
    db.run(
      favorites
        .filter(_.userId === userId)
        .filter(_.favoritedId inSet articleIds)
        .map(_.favoritedId)
        .result)

  def countFavorites(articleIds: Seq[Long]): Future[Seq[(Long, Int)]] =
    db.run(
      favorites
        .filter(_.favoritedId inSet articleIds)
        .groupBy(_.favoritedId)
        .map({ case (a, q) => (a, q.size) })
        .result)

  def findTagByNames(tagNames: Seq[String]): Future[Seq[TagV]] =
    db.run(
      tags.filter(_.name inSet tagNames).result
    )
  def insertAndGet(tagVs: Seq[TagV]): Future[Seq[TagV]] = {
    val articlesIds =
      tags.returning(tags.map(_.id))
        .++=(tagVs)
        .flatMap(ids => tags.filter(_.id inSet ids).result)

    db.run(articlesIds)
  }
//  def saveTag(tag: TagV): Future[Long] = {
//    val existsTag = tags.filter(_.id === tag.id)
////    val articleWithId = (tags returning tag.map(_.id) into ( (u,
////                                            id) => u.copy(id = id + 1))) += newArticle
//    db.run(
//      tags.filter(_.id === tag.id)
//      tags.insert(tag)
//    )
//  }

  case class MaybeFilter[X, Y](query: Query[X, Y, Seq]) {
    def filter[T, R <: Rep[_]: CanBeQueryCondition](data: Option[T])(
        f: T => X => R): MaybeFilter[X, Y] = {
      data.map(v => MaybeFilter(query.filter(f(v)))).getOrElse(this)
    }
  }

}