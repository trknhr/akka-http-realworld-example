package realworld.com.tags

import org.scalamock.scalatest.MockFactory
import realworld.com.BaseServiceTest
import realworld.com.articles.TagV
import realworld.com.utils.{ InMemoryPostgresStorage, StorageRunner }

import scala.concurrent.Future
import slick.dbio.DBIO

class TagServiceTest extends BaseServiceTest with MockFactory {
  "TagService" when {
    "getTags" should {
      "return tags" in new Context {
        (tagStorage.getTags _).expects() returning DBIO.successful {
          Seq(TagV(1, "one"), TagV(1, "two"))
        }

        awaitForResult(
          for {
            tags <- tagService.getTags()
          } yield tags shouldBe ResponseTags(List(TagV(1, "one"), TagV(1, "two")))
        )
      }
    }
  }

  trait Context {
    val tagStorage = mock[TagStorage]
    val storageRunner = new StorageRunner(
      InMemoryPostgresStorage.databaseConnector
    )
    val tagService = new TagService(storageRunner, tagStorage)
  }
}
