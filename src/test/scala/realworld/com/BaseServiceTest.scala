package realworld.com

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import org.scalatest.mockito.MockitoSugar

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by kunihiro on 2018/08/02.
  */
class BaseServiceTest extends WordSpec with Matchers with ScalatestRouteTest /*with MockitoSugar*/ {

  def awaitForResult[T](futureResult: Future[T]): T =
    Await.result(futureResult, 5.seconds)
}
