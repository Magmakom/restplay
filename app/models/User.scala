package models

import reactivemongo.bson.BSONObjectID
import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

case class User(
  _id: BSONObjectID = BSONObjectID.generate,
  name: String,
  email: String,
  loginInfo: LoginInfo
) extends Identity

object User{

  import play.api.libs.json.Json
  import play.modules.reactivemongo.json.BSONFormats._

  implicit val restaurantFormat = Json.format[User]

}
