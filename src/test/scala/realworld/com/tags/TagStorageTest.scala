package realworld.com.tags

import realworld.com.BaseServiceTest
import realworld.com.articles.TagV
import realworld.com.utils.{ DatabaseCleaner, InMemoryPostgresStorage }

class TagStorageTest extends BaseServiceTest {
  override def beforeEach(): Unit = {
    DatabaseCleaner.cleanDatabase(InMemoryPostgresStorage.databaseConnector)
    super.beforeEach()
  }

  "find tag" when {
    "findTagByNames" should {
      "should return correct an article" in new Context {
        awaitForResult(for {
          a <- tagStorage.insertAndGet(
            Seq(TagV.create("test"), TagV.create("test2"))
          )
          tags <- tagStorage.findTagByNames(Seq("test"))
        } yield tags(0).name shouldBe "test")
      }
    }
  }
  "insert tag" when {
    "insertArticleTag" should {
      "should count favorite numbers" in new Context {
        awaitForResult(for {
          tags <- tagStorage.insertAndGet(Seq(TagV.create("test")))
        } yield tags shouldBe Vector(TagV(tags.head.id, "test")))
      }
    }
  }
  "getTags" when {
    "return all tags " in new Context {
      val tags = Seq(TagV(1, "one"), TagV(2, "two"))
      awaitForResult(
        for {
          _ <- tagStorage.insertAndGet(tags)
          all <- tagStorage.getTags()
        } yield {
          all.length shouldBe tags.length
          all.map(_.name) shouldBe Seq("one", "two")
        }
      )
    }
  }

  trait Context {
    val tagStorage: TagStorage = new JdbcTagStorage(
      InMemoryPostgresStorage.databaseConnector
    )
  }
}
