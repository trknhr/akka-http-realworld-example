package realworld.com.tags

import org.scalamock.scalatest.MockFactory
import realworld.com.BaseServiceTest
import java.sql.Timestamp
import java.util.Date

import org.scalamock.scalatest.MockFactory
import realworld.com.BaseServiceTest
import realworld.com.core.User
import realworld.com.users.UserStorage

import scala.concurrent.Future

class TagServiceTest extends BaseServiceTest with MockFactory {

  "TagService" when {
    "getProfile" should {
      "return profile by id" in new Context {
        (userStorage.getUserByUsername _).expects("username-1") returning Future { Some(testUser1) }
        (userStorage.isFollowing _).expects(1, 1) returning Future { true }

        for {
          profile <- profileService.getProfile(1, "username-1")
        } {
          profile shouldBe Some(Profile("username-1", None, None, true))
        }
      }
    }
  }

  trait Context {
    val tagStorage = mock[TagStorage]
  }
}
