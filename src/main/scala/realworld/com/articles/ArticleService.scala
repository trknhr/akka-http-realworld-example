package realworld.com.articles

import scala.concurrent.Future

class ArticleService(
                    articleStorage: ArticleStorage
                    ) {

  def getArticles(autherName: Option[String]): Future[Seq[Article]] =
    articleStorage.getArticles()

}
