package models

import reactivemongo.bson.BSONObjectID

case class Restaurant(
  _id: BSONObjectID = BSONObjectID.generate,
  name: String,
  telephone: String,
  description: String,
  address: String,
  workingHours: String,
  lat: Double,
  lng: Double
)

object Restaurant{

  import play.api.libs.json.Json
  import play.modules.reactivemongo.json.BSONFormats._

  implicit val restaurantFormat = Json.format[Restaurant]

}
