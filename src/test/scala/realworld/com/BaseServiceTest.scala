package realworld.com

import java.sql.Timestamp
import java.util.Date

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import realworld.com.utils.{DatabaseCleaner, InMemoryPostgresStorage}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * Created by kunihiro on 2018/08/02.
 */
class BaseServiceTest extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter with BeforeAndAfterEach with BeforeAndAfterAll with BeforeAndAfterEachTestData with ScalaFutures {
  def awaitForResult[T](futureResult: Future[T]): T =
    Await.result(futureResult, 5.seconds)

  def currentWhenInserting = new Timestamp((new Date).getTime)
}
