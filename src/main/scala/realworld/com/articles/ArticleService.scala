package realworld.com.articles

import realworld.com.core.User
import realworld.com.profile.Profile
import realworld.com.users.UserStorage

import scala.concurrent.{ExecutionContext, Future}
import realworld.com.utils.MonadTransformers._

class ArticleService(
    articleStorage: ArticleStorage,
    userStorage: UserStorage
)(implicit executionContext: ExecutionContext) {

  def getArticles(request: ArticleRequest): Future[Seq[Article]] =
    articleStorage.getArticles(request)

  def createArticle(
      authorId: Long,
      newArticle: ArticlePosted,
      currentUserId: Option[Long]): Future[Option[ArticleForResponse]] =
    for {
      article <- articleStorage.createArticle(newArticle.create(authorId))
      tags <- createTags(newArticle.tagList)
      res <- getArticleResponse(article, tags, currentUserId)
    } yield res

  def createTags(tagNames: Seq[String]) = {
    for {
      existingTags <- articleStorage.findTagByNames(tagNames)
      newTags <- extractNewTag(tagNames, existingTags)
      tags = existingTags ++ newTags
    } yield tags
  }

  def getArticleResponse(article: Article,
                         tags: Seq[TagV],
                         currentUserId: Option[Long]) =
    userStorage
      .getUser(article.authorId)
      .flatMapTFuture(
        author => getArticleWithTags(article, author, tags, currentUserId)
      )

  def getArticleWithTags(article: Article,
                         author: User,
                         tags: Seq[TagV],
                         currentUserId: Option[Long]) =
    for {
      favorites <- articleStorage.isFavoriteArticleIds(
        currentUserId.getOrElse(0),
        Seq(article.id))
      favoriteCount <- articleStorage.countFavorites(Seq(article.id))
    } yield {
      ArticleForResponse(
        article.slug,
        article.title,
        article.description,
        article.body,
        tags.map(t => t.name),
        article.createdAt,
        article.updatedAt,
        favorites.contains(article.id),
        favoriteCount.map(a => a._1 -> a._2).toMap.get(article.id).getOrElse(0),
        Profile(
          author.username,
          author.bio,
          author.image,
          false
        )
      )
    }

  def extractNewTag(tagNames: Seq[String], existingTags: Seq[TagV]) = {
    val existingTagNames = existingTags.map(_.name).toSet
    val newTagNames = tagNames.toSet -- existingTagNames
    val newTags = newTagNames.map(TagV.create).toSeq

    articleStorage.insertAndGet(newTags)
  }

  def getFeeds(userId: Long,
               limit: Option[Int],
               offset: Option[Int]): Future[Seq[ArticleForResponse]] =
    for {
      articles <- articleStorage.getArticlesByFollowees(userId, limit, offset)
      favorites <- articleStorage
        .isFavoriteArticleIds(userId, articles.map(_.authorId))
        .map(_.toSet)
      favoriteCount <- articleStorage.countFavorites(articles.map(_.authorId))
    } yield {
      articles.map(
        a =>
          ArticleForResponse(
            a.slug,
            a.title,
            a.description,
            a.body,
            Seq(""),
            a.createdAt,
            a.updatedAt,
            favorites.contains(a.id),
            favoriteCount.map(a => a._1 -> a._2).toMap.get(a.id).getOrElse(0),
            Profile("dummy", None, None, false)
        ))
    }

  def getArticleBySlug(slug: String): Future[Option[Article]] =
    articleStorage.getArticleBySlug(slug)

}
