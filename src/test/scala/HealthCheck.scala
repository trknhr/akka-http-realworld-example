/**
  * Created by kunihiro on 2018/08/02.
  */
class HealthCheck extends BaseServiceTest {
  InmemoryPostgresStorage
  implicit val sttpBackend = AkkaHttpBackend()

  "reqlest to health checks" in {
    200 shouldBe 200

  }
}
