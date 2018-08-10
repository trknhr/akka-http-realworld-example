package realworld.com.profile

import java.sql.JDBCType

import realworld.com.BaseServiceTest
import realworld.com.utils.InMemoryPostgresStorage

class ProfileStorageTest extends BaseServiceTest{

  "profileStorage" when {
    "getProfile" should {
      "return profile by id" in new Context {
        awaitForResult(for {
          maybeProfile <- profileStorage.getProfile(testProfileUsername)
        } yield maybeProfile shouldBe None)
      }
    }
  }
  trait Context {
    val profileStorage: ProfileStorage = new JdbcProfileStorage(InMemoryPostgresStorage.databaseConnector)
    val testProfileUsername = "profilename1"
    val testProfileUsername2 = "profilename2"

    val testProfile1: Profile = testProfile(testProfileUsername)
    val testProfile2: Profile = testProfile(testProfileUsername2)

    def testProfile(username: String)  = Profile(username, Some("bio"), Some("image"), false)
  }
}

