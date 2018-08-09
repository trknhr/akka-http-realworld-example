package realworld.com.profile

import java.sql.JDBCType

import realworld.com.BaseServiceTest
import realworld.com.utils.InMemoryPostgresStorage

class ProfileStorageTest extends BaseServiceTest{

  "profileStorage" when {
    "getProfile" should {
      "return profile by id" in new Context {
//        awaitForResult(for {
//          _ <- profileStorage.getProfile(testProfileId)
//
//        })

      }
    }
  }
  trait Context {
    val profileStorage: ProfileStorage = new JdbcProfileStorage(InMemoryPostgresStorage.databaseConnector)
    val testProfileId = 1
    val testProfile2Id = 2

    val testProfile1: Profile = testProfile(testProfileId)
    val testProfile2: Profile = testProfile(testProfile2Id)

    def testProfile(id: Long)  = Profile("username", Some("bio"), Some("image"), false)
  }
}

