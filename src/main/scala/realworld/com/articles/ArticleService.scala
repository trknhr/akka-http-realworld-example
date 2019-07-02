package realworld.com.articles

import realworld.com.core.User
import realworld.com.profile.Profile
import realworld.com.tags.TagStorage
import realworld.com.users.UserStorage
import realworld.com.utils.{ FutureOptional, ISO8601 }

import scala.concurrent.{ ExecutionContext, Future }

class ArticleService(
    articleStorage: ArticleStorage,
    userStorage: UserStorage,
    tagStorage: TagStorage
)(implicit executionContext: ExecutionContext) {

  def getArticles(request: ArticleRequest): Future[ForResponseArticles] = {
    for {
      articles <- articleStorage.getArticles(request)
      authors <- userStorage
        .getUsersByUserIds(articles.map(_.authorId))
        .map(a => a map (t => t.id -> t) toMap)
      tags <- tagStorage.getTagsByArticles(articles.map(_.id))
    } yield {
      ForResponseArticles(
        articles.map(
          a => {
            ArticleForResponse(
              a.slug,
              a.title,
              a.description,
              a.body,
              tags.filter(_._1 == a.id).map(_._2.name),
              ISO8601(a.createdAt),
              ISO8601(a.updatedAt),
              false,
              0,
              convertUserToProfile(authors.get(a.authorId))
            )
          }
        ),
        articles.length
      )
    }
  }

  def createArticle(
    authorId: Long,
    newArticle: ArticlePosted,
    currentUserId: Option[Long]
  ): Future[Option[ForResponseArticle]] =
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
  ): Future[ForResponseArticles] =
    for {
      articles <- articleStorage.getArticlesByFollowees(userId, limit, offset)
      favorites <- articleStorage
        .isFavoriteArticleIds(userId, articles.map(_.authorId))
      favoriteCount <- articleStorage.countFavorites(articles.map(_.authorId))
      authors <- userStorage
        .getUsersByUserIds(Seq(1, 2))
      tags <- tagStorage.getTagsByArticles(articles.map(_.id))
    } yield {
      val mapAuthor = authors.map(t => t.id -> t).toMap

      ForResponseArticles(
        articles.map(
          a =>
            ArticleForResponse(
              a.slug,
              a.title,
              a.description,
              a.body,
              tags.filter(_._1 == a.id).map(_._2.name),
              ISO8601(a.createdAt),
              ISO8601(a.updatedAt),
              favorites.toSet.contains(a.id),
              favoriteCount.map(a => a._1 -> a._2).toMap.get(a.id).getOrElse(0),
              convertUserToProfile(mapAuthor.get(a.authorId))
            )
        ),
        articles.length
      )
    }

  def getArticleBySlug(
    slug: String,
    userId: Long
  ): Future[Option[ForResponseArticle]] =
    (for {
      article <- FutureOptional(articleStorage.getArticleBySlug(slug))
      f <- FutureOptional(
        articleStorage
          .favoriteArticle(
            userId,
            article.id
          )
          .map(Some(_))
      )
      favoriteCount <- FutureOptional(
        articleStorage
          .countFavorite(
            article.authorId
          )
          .map(Some(_))
      )
      author <- FutureOptional(
        userStorage
          .getUser(article.authorId)
      )
      tags <- FutureOptional(
        tagStorage.getTagsByArticle(article.id).map(Some(_))
      )
    } yield ForResponseArticle(
      ArticleForResponse(
        article.slug,
        article.title,
        article.description,
        article.body,
        tags.map(_.name),
        ISO8601(article.createdAt),
        ISO8601(article.updatedAt),
        f.favoritedId == article.id,
        favoriteCount,
        convertUserToProfile(Some(author))
      )
    )).future

  def updateArticleBySlug(
    slug: String,
    userId: Long,
    articleUpdated: ArticleUpdated
  ): Future[Option[ForResponseArticle]] =
    (for {
      a <- FutureOptional(articleStorage.getArticleBySlug(slug))
      article <- FutureOptional(
        articleStorage
          .updateArticle(
            updateArticle(a, articleUpdated)
          )
          .map(Some(_))
      )
      f <- FutureOptional(
        articleStorage
          .favoriteArticle(
            userId,
            article.id
          )
          .map(Some(_))
      )
      favoriteCount <- FutureOptional(
        articleStorage
          .countFavorite(
            article.authorId
          )
          .map(Some(_))
      )
      author <- FutureOptional(
        userStorage
          .getUser(article.authorId)
      )
      tags <- FutureOptional(
        tagStorage.getTagsByArticle(article.id).map(Some(_))
      )
    } yield ForResponseArticle(
      ArticleForResponse(
        article.slug,
        article.title,
        article.description,
        article.body,
        tags.map(_.name),
        ISO8601(article.createdAt),
        ISO8601(article.updatedAt),
        f.favoritedId == article.id,
        favoriteCount,
        convertUserToProfile(Some(author))
      )
    )).future
  //    articleStorage
  //      .getArticleBySlug(slug)
  //      .flatMapTFuture(a =>
  //        for {
  //          article <- articleStorage.updateArticle(
  //            updateArticle(a, articleUpdated)
  //          )
  //          f <- articleStorage.favoriteArticle(
  //            userId,
  //            article.id
  //          )
  //          favoriteCount <- articleStorage.countFavorite(
  //            article.authorId
  //          )
  //          author <- userStorage
  //            .getUser(article.authorId)
  //          tags <- tagStorage.getTagsByArticle(article.id)
  //        } yield {
  //          ForResponseArticle(
  //            ArticleForResponse(
  //              article.slug,
  //              article.title,
  //              article.description,
  //              article.body,
  //              tags.map(_.name),
  //              ISO8601(article.createdAt),
  //              ISO8601(article.updatedAt),
  //              f.favoritedId == article.id,
  //              favoriteCount,
  //              convertUserToProfile(author)
  //            )
  //          )
  ////      })

  def deleteArticleBySlug(slug: String): Future[Unit] =
    articleStorage.deleteArticleBySlug(slug)

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
      author <- userStorage
        .getUser(article.map(_.authorId).getOrElse(-1L))
      tags <- tagStorage.getTagsByArticle(article.map(_.id).getOrElse(-1L))
    } yield article.map(
      a =>
        ForResponseArticle(
          ArticleForResponse(
            a.slug,
            a.title,
            a.description,
            a.body,
            tags.map(_.name),
            ISO8601(a.createdAt),
            ISO8601(a.updatedAt),
            true,
            favoriteCount + 1,
            convertUserToProfile(author)
          )
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
      author <- userStorage
        .getUser(article.map(_.authorId).getOrElse(-1L))
      tags <- tagStorage.getTagsByArticle(article.map(_.id).getOrElse(-1L))
    } yield article.map(
      a =>
        ForResponseArticle(
          ArticleForResponse(
            a.slug,
            a.title,
            a.description,
            a.body,
            tags.map(_.name),
            ISO8601(a.createdAt),
            ISO8601(a.updatedAt),
            false,
            favoriteCount,
            convertUserToProfile(author)
          )
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
      existingTags <- tagStorage.findTagByNames(tagNames)
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

    tagStorage.insertAndGet(newTags)
  }

  private def getArticleResponse(
    article: Article,
    tags: Seq[TagV],
    currentUserId: Option[Long]
  ): Future[Option[ForResponseArticle]] =
    (for {
      u <- FutureOptional(userStorage.getUser(article.authorId))
      a <- FutureOptional(
        getArticleWithTags(article, u, tags, currentUserId).map(Some(_))
      )
    } yield a).future

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
      ForResponseArticle(
        ArticleForResponse(
          article.slug,
          article.title,
          article.description,
          article.body,
          tags.map(t => t.name),
          ISO8601(article.createdAt),
          ISO8601(article.updatedAt),
          favorites.contains(article.id),
          favoriteCount
            .map(a => a._1 -> a._2)
            .toMap
            .get(article.id)
            .getOrElse(0),
          Profile(
            author.username,
            author.bio,
            author.image,
            false
          )
        )
      )
    }

  private def convertUserToProfile(author: Option[User]) =
    author match {
      case Some(a) => Profile(a.username, a.bio, a.image, false)
      case None => Profile("", None, None, false)
    }

}
