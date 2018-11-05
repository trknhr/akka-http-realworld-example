package realworld.com.articles

import realworld.com.profile.Profile

import scala.concurrent.{ExecutionContext, Future}

class ArticleService(
    articleStorage: ArticleStorage
) (implicit executionContext: ExecutionContext){

  def getArticles(request: ArticleRequest): Future[Seq[Article]] =
    articleStorage.getArticles(request)

  def createArticle(authorId: Long,
                    newArticle: ArticlePosted, tagNames: Seq[String]): Future[Article] =
    for{
      article <- articleStorage.createArticle(newArticle.create(authorId))
      tags <- createTags(tagNames)
    } yield article

  def createTags(tagNames: Seq[String]) = {
    for{
      existingTags <- articleStorage.findTagByNames(tagNames)
      newTags <- extractNewTag(tagNames, existingTags)
      tags = existingTags ++ newTags
    }
  }

  def getArticleResponse(article: Article, tags: Seq[TagV])

//  def associateTagsWithArticle(tags: Seq[Tag0], article: Article)  = {
//
//  }


  def extractNewTag(tagNames: Seq[String], existingTags: Seq[TagV]) = {
    val existingTagNames = existingTags.map(_.name).toSet
    val newTagNames = tagNames.toSet -- existingTagNames
    val newTags = newTagNames.map(TagV.create).toSeq

    articleStorage.insertAndGet(newTags)
  }

  def getFeeds(userId: Long,
               limit: Option[Int],
               offset: Option[Int]): Future[Seq[ArticleForResponse]] =
    for{
      articles <- articleStorage.getArticlesByFollowees(userId, limit, offset)
      favorites <- articleStorage.isFavoriteArticleIds(userId, articles.map(_.authorId)).map(_.toSet)
      favoriteCount <- articleStorage.countFavorites(articles.map(_.authorId))
    } yield {
      articles.map( a =>
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
          Profile("dummy",
          None,
          None,
          false)
        )

      )
    }

  def getArticleBySlug(slug: String): Future[Option[Article]] =
    articleStorage.getArticleBySlug(slug)

}
