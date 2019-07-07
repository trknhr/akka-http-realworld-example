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
        (userStorage.getUserByUsername _)
          .expects("username-1") returning Future { Some(testUser1) }
        (userStorage.isFollowing _).expects(*, *) returning Future { true }

        awaitForResult(for {
          profile <- profileService.getProfile(1, "username-1")
        } yield {
          profile shouldBe Some(
            ResponseProfile(Profile("username-1", None, None, true))
          )
        })
      }
    }
    "follow" should {
      "return profile with follow true" in new Context {
        (userStorage.getUserByUsername _)
          .expects("username-1") returning Future { Some(testUser1) }
        (userStorage.follow _).expects(*, *) returning Future { 1 }

        awaitForResult(for {
          profile <- profileService.follow(1, "username-1")
        } yield {
          profile shouldBe Some(
            ResponseProfile(Profile("username-1", None, None, true))
          )
        })
      }
    }
    "unfollow" should {
      "return profile with follow false" in new Context {
        (userStorage.getUserByUsername _)
          .expects("username-1") returning Future { Some(testUser1) }
        (userStorage.unfollow _).expects(*, *) returning Future { 1 }

        awaitForResult(for {
          profile <- profileService.unfollow(1, "username-1")
        } yield {
          profile shouldBe Some(
            ResponseProfile(Profile("username-1", None, None, false))
          )
        })
      }
    }
    "getFollowees" should {
      "return profiles" in new Context {
        (userStorage.getFollowees _).expects(*) returning Future {
          Seq(testUser1)
        }
        awaitForResult(for {
          profiles <- profileService.getFollowees(1)
        } yield {
          profiles.length shouldBe 1
          profiles.head.username shouldBe testUser1.username
        })
      }

    }
  }

  trait Context {
    val userStorage = mock[UserStorage]
    val profileService = new ProfileService(userStorage)

    def currentWhenInserting = new Timestamp((new Date).getTime)
    def testUser(testUser: TestUser) =
      User(
        testUser.userId,
        testUser.username,
        testUser.password,
        testUser.email,
        None,
        image = None,
        createdAt = currentWhenInserting,
        updatedAt = currentWhenInserting
      )

    val testUser1 = testUser(
      TestUser(1, "username-1", "username-email-1", "user-password-1")
    )
    val testUser2 = testUser(
      TestUser(2, "username-2", "username-email-2", "user-password-2")
    )

    case class TestUser(
      userId: Long,
      username: String,
      email: String,
      password: String
    )
  }
}
