package realworld.com

import java.sql.Timestamp
import java.util.Date

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import org.scalatest.mockito.MockitoSugar
import realworld.com.utils.{ DatabaseCleaner, InMemoryPostgresStorage }

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

/**
 * Created by kunihiro on 2018/08/02.
 */
class BaseServiceTest extends WordSpec with Matchers with ScalatestRouteTest {

  def databaseTest[T](futureResult: Future[T]): T = {
    var x = Await.result(futureResult, 5.seconds)
    DatabaseCleaner.cleanDatabase(InMemoryPostgresStorage.databaseConnector)
    x
  }

  def awaitForResult[T](futureResult: Future[T]): T =
    Await.result(futureResult, 5.seconds)

  def currentWhenInserting = new Timestamp((new Date).getTime)
}
