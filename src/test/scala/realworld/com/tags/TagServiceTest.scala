package realworld.com.tags

import org.scalamock.scalatest.MockFactory
import realworld.com.BaseServiceTest
import realworld.com.articles.TagV

import scala.concurrent.Future

class TagServiceTest extends BaseServiceTest with MockFactory {
  "TagService" when {
    "getTags" should {
      "return tags" in new Context {
        (tagStorage.getTags _).expects() returning Future {
          Seq(TagV(1, "one"), TagV(1, "two"))
        }

        awaitForResult(
          for {
            tags <- tagService.getTags()
          } yield
            tags shouldBe ResponseTags(List(TagV(1, "one"), TagV(1, "two")))
        )
      }
    }
  }

  trait Context {
    val tagStorage = mock[TagStorage]
    val tagService = new TagService(tagStorage)
  }
}
