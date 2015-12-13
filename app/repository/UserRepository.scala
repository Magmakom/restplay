package repository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import play.api.libs.json._
import play.modules.reactivemongo.json.BSONFormats._
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.bson.BSONObjectID
import reactivemongo.extensions.json.dao.JsonDao
import reactivemongo.extensions.json.dsl.JsonDsl._

import models.User
import services.UserService

class UserRepository extends JsonDao[User, BSONObjectID](MongoEnv.db, "users")
  with UserService {

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def retrieve(loginInfo: LoginInfo) : Future[Option[User]] = {
    findOne(selector = "loginInfo" $eq loginInfo)
  }

}
