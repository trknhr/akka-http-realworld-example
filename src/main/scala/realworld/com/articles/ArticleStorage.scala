package realworld.com.articles

import realworld.com.articles.comments.CommentTable
import realworld.com.profile.UserFollowersTable
import realworld.com.users.UserProfileTable
import realworld.com.utils.DatabaseConnector
import slick.dbio.DBIOAction
import slick.lifted.CanBeQueryCondition

import scala.concurrent.{ ExecutionContext, Future }

trait ArticleStorage {
  def getArticles(pageRequest: ArticleRequest): Future[Seq[Article]]
  def getArticlesByFollowees(
    userId: Long,
    limit: Option[Int],
    offset: Option[Int]
  ): Future[Seq[Article]]
  def createArticle(newArticle: Article): Future[Article]
  def getArticleBySlug(slug: String): Future[Option[Article]]
  def updateArticle(article: Article): Future[Article]
  def isFavoriteArticleIds(
    userId: Long,
    articleIds: Seq[Long]
  ): Future[Seq[Long]]
  def favoriteArticle(userId: Long, articleId: Long): Future[Favorite];
  def unFavoriteArticle(userId: Long, articleId: Long): Future[Int];
  def countFavorites(articleIds: Seq[Long]): Future[Seq[(Long, Int)]]
  def countFavorite(articleId: Long): Future[Int]
  def insertArticleTag(atags: Seq[ArticleTag]): Future[Seq[ArticleTag]]
  def deleteArticleBySlug(slug: String): Future[Unit]
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
    with FavoriteTable
    with CommentTable {
  import databaseConnector._
  import databaseConnector.profile.api._

  def getArticles(pageRequest: ArticleRequest): Future[Seq[Article]] = {
    val query = articles.join(users).on(_.authorId === _.id)

    db.run(
      query.filter { st =>
      pageRequest.authorName.fold(true.bind)(st._2.username === _)
    }.filter { st =>
      pageRequest.tag.fold(true.bind) { tag =>
        st._1.id in articleTags.join(tags).on(_.tagId === _.id).filter(_._2.name === tag).map(_._1.articleId)
      }
    }.filter { st =>
      pageRequest.favorited.fold(true.bind) { favoritedUsername =>
        st._1.id in users.filter(_.username === favoritedUsername).map(_.id)
      }
    }.map(_._1)
      .drop(pageRequest.offset.getOrElse(0L))
      .take(pageRequest.limit.getOrElse(Long.MaxValue))
      .result
    )
  }

  def getArticlesByFollowees(
    userId: Long,
    limit: Option[Int],
    offset: Option[Int]
  ): Future[Seq[Article]] =
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
        (
          u,
          id
        ) => u.copy(id = id)
      )) += newArticle

    db.run(articleWithId)
  }

  private def getArticleById(id: Long) =
    db.run(articles.filter(_.id === id).result.headOption)

  def updateArticle(article: Article): Future[Article] = {
    db.run(articles
      .filter(_.id === article.id)
      .update(article)
      .flatMap(_ => articles.filter(_.id === article.id).result.head))
  }

  def getArticleBySlug(slug: String): Future[Option[Article]] = {
    db.run(articles.filter(_.slug === slug).result.headOption)
  }

  def isFavoriteArticleIds(
    userId: Long,
    articleIds: Seq[Long]
  ): Future[Seq[Long]] =
    db.run(
      favorites
      .filter(_.userId === userId)
      .filter(_.favoritedId inSet articleIds)
      .map(_.favoritedId)
      .result
    )

  def countFavorites(articleIds: Seq[Long]): Future[Seq[(Long, Int)]] =
    db.run(
      favorites
      .filter(_.favoritedId inSet articleIds)
      .groupBy(_.favoritedId)
      .map({ case (a, q) => (a, q.size) })
      .result
    )

  def countFavorite(articleId: Long): Future[Int] =
    db.run(
      favorites
      .filter(_.favoritedId === articleId)
      .length
      .result
    )

  def insertArticleTag(atags: Seq[ArticleTag]): Future[Seq[ArticleTag]] =
    db.run(articleTags
      .returning(articleTags.map(_.id))
      .++=(atags)
      .flatMap(ids => articleTags.filter(_.id inSet ids).result))

  def deleteArticleBySlug(slug: String): Future[Unit] = {
    val deleteA = articleTags.filter(_.articleId in articles.filter(a => a.slug === slug).map(_.id)).delete
    val deleteB = favorites.filter(_.favoritedId in articles.filter(a => a.slug === slug).map(_.id)).delete
    val deleteC = comments.filter(_.articleId in articles.filter(a => a.slug === slug).map(_.id)).delete
    val deleteD = articles.filter(_.slug === slug).delete

    db.run(
      DBIOAction.seq(deleteA, deleteB, deleteC, deleteD).transactionally
    )
  }

  def favoriteArticle(userId: Long, articleId: Long): Future[Favorite] = {
    val insertFavorites = favorites returning favorites.map(_.id) into ((favorite, id) => favorite.copy(id = id))
    db.run(insertFavorites += Favorite(-1, userId, articleId))
  }

  def unFavoriteArticle(userId: Long, articleId: Long): Future[Int] =
    db.run(favorites.filter(a => a.userId === userId && a.favoritedId === articleId).delete)
}
