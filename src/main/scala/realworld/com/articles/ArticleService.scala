package realworld.com.articles

import realworld.com.core.User
import realworld.com.profile.Profile
import realworld.com.users.UserStorage
import realworld.com.utils.ISO8601

import scala.concurrent.{ExecutionContext, Future}
import realworld.com.utils.MonadTransformers._

class ArticleService(
    articleStorage: ArticleStorage,
    userStorage: UserStorage
)(implicit executionContext: ExecutionContext) {

  def getArticles(request: ArticleRequest): Future[ForResponseArticles] = {
    for {
      articles <- articleStorage.getArticles(request)
      authors <- userStorage
                  .getUsers(articles.map(_.authorId)).map(a => a map (t => t.id -> t) toMap)
    } yield {
      ForResponseArticles(articles.map(
        a => {
          // Todo favorited
          // Author
          val targetAuthor = authors.get(a.authorId)
          ArticleForResponse(
            a.slug,
            a.title,
            a.description,
            a.body,
            Seq(""),
            ISO8601(a.createdAt),
            ISO8601(a.updatedAt),
            false,
            0,
            createProfile(targetAuthor)//Profile(author.username, author.bio, author.image, false)
          )
        }
      ), articles.length)
    }
  }

  def createProfile(author: Option[User]) =
    author match {
      case Some(a) => Profile(a.username, a.bio, a.image, false)
      case None => Profile("", None, None, false)
  }



  def createArticle(
    authorId: Long,
    newArticle: ArticlePosted,
    currentUserId: Option[Long]
  ): Future[Option[ArticleForResponse]] =
    for {
      article <- articleStorage.createArticle(newArticle.create(authorId))
      tags <- createTags(newArticle.tagList)
      _ <- connectTagArticle(tags, article.id)
      res <- getArticleResponse(article, tags, currentUserId)
    } yield res

  def getFeeds(
    userId: Long,
    limit: Option[Int],
    offset: Option[Int]
  ): Future[Seq[ArticleForResponse]] =
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
            ISO8601(a.createdAt),
            ISO8601(a.updatedAt),
            favorites.contains(a.id),
            favoriteCount.map(a => a._1 -> a._2).toMap.get(a.id).getOrElse(0),
            Profile("dummy", None, None, false)
          )
      )
    }

  def getArticleBySlug(slug: String): Future[Option[Article]] =
    articleStorage.getArticleBySlug(slug)

  def updateArticleBySlug(
    slug: String,
    articleUpdated: ArticleUpdated
  ): Future[Option[Article]] =
    articleStorage
      .getArticleBySlug(slug)
      .flatMapTFuture(
        a => articleStorage.updateArticle(updateArticle(a, articleUpdated))
      )

  def deleteArticleBySlug(slug: String): Future[Unit] =
    articleStorage.deleteArticleBySlug(slug).map(_ => {})

  def favoriteArticle(userId: Long, slug: String) =
    for {
      article <- articleStorage.getArticleBySlug(slug)
      f <- articleStorage.favoriteArticle(
        userId,
        article.map(b => b.id).getOrElse(-1L)
      )
      favoriteCount <- articleStorage.countFavorite(
        article.map(_.authorId).getOrElse(-1L)
      )
    } yield article.map(
      a =>
        ArticleForResponse(
          a.slug,
          a.title,
          a.description,
          a.body,
          Seq(""),
          ISO8601(a.createdAt),
          ISO8601(a.updatedAt),
          true,
          favoriteCount,
          Profile("dummy", None, None, false)
        )
    )

  def unFavoriteArticle(userId: Long, slug: String) =
    for {
      article <- articleStorage.getArticleBySlug(slug)
      f <- articleStorage.unFavoriteArticle(
        userId,
        article.map(b => b.id).getOrElse(-1L)
      )
      favoriteCount <- articleStorage.countFavorite(
        article.map(_.authorId).getOrElse(-1L)
      )
    } yield article.map(
      a =>
        ArticleForResponse(
          a.slug,
          a.title,
          a.description,
          a.body,
          Seq(""),
          ISO8601(a.createdAt),
          ISO8601(a.updatedAt),
          true,
          favoriteCount,
          Profile("dummy", None, None, false)
        )
    )

  private def updateArticle(
    article: Article,
    update: ArticleUpdated
  ): Article = {
    val title = update.title.getOrElse(article.title)
    val slug = slugify(title)
    val description = update.description.getOrElse(article.description)
    val body = update.body.getOrElse(article.body)

    article.copy(
      title = title,
      slug = slug,
      description = description,
      body = body
    )
  }

  private def createTags(tagNames: Seq[String]) =
    for {
      existingTags <- articleStorage.findTagByNames(tagNames)
      newTags <- extractNewTag(tagNames, existingTags)
      tags = existingTags ++ newTags
    } yield tags

  private def connectTagArticle(tags: Seq[TagV], articleId: Long) = {
    val articleTags = tags.map(tag => ArticleTag(-1, articleId, tag.id))
    articleStorage.insertArticleTag(articleTags)
  }

  private def extractNewTag(tagNames: Seq[String], existingTags: Seq[TagV]) = {
    val existingTagNames = existingTags.map(_.name).toSet
    val newTagNames = tagNames.toSet -- existingTagNames
    val newTags = newTagNames.map(TagV.create).toSeq

    articleStorage.insertAndGet(newTags)
  }

  private def getArticleResponse(
    article: Article,
    tags: Seq[TagV],
    currentUserId: Option[Long]
  ) =
    userStorage
      .getUser(article.authorId)
      .flatMapTFuture(
        author => getArticleWithTags(article, author, tags, currentUserId)
      )

  private def getArticleWithTags(
    article: Article,
    author: User,
    tags: Seq[TagV],
    currentUserId: Option[Long]
  ) =
    for {
      favorites <- articleStorage.isFavoriteArticleIds(
        currentUserId.getOrElse(0),
        Seq(article.id)
      )
      favoriteCount <- articleStorage.countFavorites(Seq(article.id))
    } yield {
      ArticleForResponse(
        article.slug,
        article.title,
        article.description,
        article.body,
        tags.map(t => t.name),
        ISO8601(article.createdAt),
        ISO8601(article.updatedAt),
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

}
