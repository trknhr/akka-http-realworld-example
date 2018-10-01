package realworld.com.articles

import scala.concurrent.Future

class ArticleService(
    articleStorage: ArticleStorage
) {

  def getArticles(request: ArticleRequest): Future[Seq[Article]] =
    articleStorage.getArticles(request)

  def createArticle(authorId: Long,
                    newArticle: ArticlePosted): Future[Article] =
    articleStorage.createArticle(newArticle.create(authorId))

  def getFeeds(userId: Long,
               limit: Option[Int],
               offset: Option[Int]): Future[Seq[Article]] =
    articleStorage.getArticlesByFollowees(userId, limit, offset)

  def getArticleBySlug(slug: String): Future[Option[Article]] =
    articleStorage.getArticleBySlug(slug)

}
