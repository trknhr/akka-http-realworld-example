package profile


import scala.concurrent.{ExecutionContext, Future}
import com.roundeights.hasher.Implicits._
import pdi.jwt.{Jwt, JwtAlgorithm}
import users.core._
import utils.MonadTransformers._
import io.circe.syntax._
import io.circe.generic.auto._
import profile.core.Profile

//class UserService(userStorage: UserStorage, secretKey: String)(implicit executionContext: ExecutionContext) {
/**
  * Created by kunihiro on 2018/07/20.
  */
class ProfileService(profileStorage: ProfileStorage, secretKey: String) (implicit executionContext: ExecutionContext) {
  def getProfile(username: String): Future[Option[Profile]] =
    profileStorage.getProfile(username)

}
