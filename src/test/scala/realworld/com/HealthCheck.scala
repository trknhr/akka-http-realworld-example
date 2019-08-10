package realworld.com

import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import realworld.com.utils.InMemoryPostgresStorage

class HealthCheck extends BaseServiceTest {

  InMemoryPostgresStorage
  implicit val sttpBackend = AkkaHttpBackend()

  "Service" should {
    "request to health checks" in {
      200 shouldBe 200
    }
  }
}
