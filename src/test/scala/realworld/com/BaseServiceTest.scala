package realworld.com

import java.sql.Timestamp
import java.util.Date

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{ BeforeAndAfter, _ }
import org.scalatest.concurrent.ScalaFutures
import realworld.com.utils.{ InMemoryPostgresStorage, StorageRunner }
import slick.dbio.DBIO

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

class BaseServiceTest
  extends WordSpec
  with Matchers
  with ScalatestRouteTest
  with BeforeAndAfter
  with BeforeAndAfterEach
  with BeforeAndAfterAll
  with BeforeAndAfterEachTestData
  with ScalaFutures {

  val runner = new StorageRunner(InMemoryPostgresStorage.databaseConnector)

  def awaitForResult[T](futureResult: Future[T]): T =
    Await.result(futureResult, 5.seconds)

  def dbRun[T](dbio: DBIO[T]): T = {
    Await.result(
      runner.run(
        dbio),
      5.seconds)
  }

  def currentWhenInserting = new Timestamp((new Date).getTime)
}
