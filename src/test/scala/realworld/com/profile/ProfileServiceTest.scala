package realworld.com.profile

import java.sql.Timestamp
import java.util.Date

import org.scalamock.scalatest.MockFactory
import realworld.com.BaseServiceTest
import realworld.com.core.User
import realworld.com.users.UserStorage

import scala.concurrent.Future

class ProfileServiceTest extends BaseServiceTest with MockFactory {

  "ProfileService" when {
    "getProfile" should {
      "return profile by id" in new Context {
        (userStorage.getUserByUsername _).expects("username-1") returning Future{Some(testUser1)}
        (userStorage.isFollowing _).expects(1, 1) returning Future{true}

        for{
          profile <- profileService.getProfile(1, "username-1")
        } {
          profile shouldBe Some(Profile("username-1", None, None, true))
        }
      }
    }
  }

  trait Context {
    val userStorage = mock[UserStorage]
    val profileService = new ProfileService(userStorage)

    def currentWhenInserting = new Timestamp((new Date).getTime)
    def testUser(testUser: TestUser) = User(testUser.userId, testUser.username, testUser.password, testUser.email, None, image = None, createdAt = currentWhenInserting, updatedAt = currentWhenInserting)

    val testUser1 = testUser(TestUser(1, "username-1", "username-email-1", "user-password-1"))
    val testUser2 = testUser(TestUser(2, "username-2", "username-email-2", "user-password-2"))


    case class TestUser(userId: Long, username: String, email: String, password: String)
  }
}
