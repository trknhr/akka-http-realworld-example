import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import utils.InMemoryPostgresStorage

/**
  * Created by kunihiro on 2018/08/02.
  */
class HealthCheck extends BaseServiceTest {

  InMemoryPostgresStorage
  implicit val sttpBackend = AkkaHttpBackend()

  "Service" should {
    "reqlest to health checks" in {
      200 shouldBe 200
    }
  }
}
