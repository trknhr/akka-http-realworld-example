package realworld.com.articles

import realworld.com.users.UserStorage

import scala.concurrent.Future
import realworld.com.utils.MonadTransformers._

class ArticleService(
  articleStorage: ArticleStorage,
) {

  def getArticles(request: ArticleRequest): Future[Seq[Article]] =
      articleStorage.getArticles(request)

  def createArticle(authorId: Long, newArticle: ArticlePosted): Future[Article] =
    articleStorage.createArticle(newArticle.create(authorId))

}
