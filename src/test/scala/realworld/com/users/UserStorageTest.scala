package realworld.com.users

import java.sql.{ JDBCType, Timestamp }
import java.util.Date

import realworld.com.BaseServiceTest
import realworld.com.core.User
import realworld.com.profile.Profile
import realworld.com.utils.{ DatabaseCleaner, InMemoryPostgresStorage }

class UserStorageTest extends BaseServiceTest {
  override def beforeEach(): Unit = {
    DatabaseCleaner.cleanDatabase(InMemoryPostgresStorage.databaseConnector)
    super.beforeEach()
  }

  "getUserByUsername" when {
    "return profile by id" in new Context {
      awaitForResult(for {
        users <- userStorage.getUsers()
        _ <- userStorage.saveUser(testUser1)
        _ <- userStorage.saveUser(testUser2)
        maybeProfile <- userStorage.getUserByUsername(testUser2.username)
      } yield {
        println("aaaaaaaa", users)
        maybeProfile shouldBe Some(testUser2)
      })
    }
  }
  "follow" when {
    "success" in new Context {
      awaitForResult(for {
        a <- userStorage.saveUser(testUser1)
        b <- userStorage.saveUser(testUser2)
        successFlag <- userStorage.follow(a.id, b.id)
      } yield {
        successFlag shouldBe 1
      })
    }
  }

  "isFollowing" when {
    "return true" in new Context {
      awaitForResult(for {
        a <- userStorage.saveUser(testUser1)
        b <- userStorage.saveUser(testUser2)
        _ <- userStorage.follow(a.id, b.id)
        isFollowing <- userStorage.isFollowing(a.id, b.id)
      } yield true shouldBe true)
    }

    "return false" in new Context {
      awaitForResult(for {
        a <- userStorage.saveUser(testUser1)
        b <- userStorage.saveUser(testUser2)
        _ <- userStorage.follow(a.id, b.id)
        isFollowing <- userStorage.isFollowing(b.id, a.id)
      } yield isFollowing shouldBe false)
    }
  }
  trait Context {
    val userStorage: UserStorage = new JdbcUserStorage(InMemoryPostgresStorage.databaseConnector)

    def currentWhenInserting = new Timestamp((new Date).getTime)
    def testUser(testUser: TestUser) = User(testUser.userId, testUser.username, testUser.password, testUser.email, None, image = None, createdAt = currentWhenInserting, updatedAt = currentWhenInserting)

    val testUser1 = testUser(TestUser(1, "username-1", "username-email-1", "user-password-1"))
    val testUser2 = testUser(TestUser(2, "username-2", "username-email-2", "user-password-2"))

    case class TestUser(userId: Long, username: String, email: String, password: String)
  }
}
