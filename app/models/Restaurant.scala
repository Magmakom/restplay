package models

import scala.collection.Seq
import reactivemongo.bson.BSONObjectID

case class Restaurant(
  _id: BSONObjectID = BSONObjectID.generate,
  name: String,
  telephone: String,
  description: String,
  address: String,
  workingHours: String,
  loc: Seq[Double]
)

object Restaurant{

  import play.api.libs.json.Json
  import play.modules.reactivemongo.json.BSONFormats._

  implicit val restaurantFormat = Json.format[Restaurant]

  import play.api.data._
  import play.api.data.format.Formats._
  import play.api.data.Forms._
  import helpers.BSONObjectIDHelper.BSONObjectIDFormFormat

  val form = Form(
    mapping(
      "id" -> ignored(BSONObjectID.generate),
      "name" -> text,
      "telephone" -> text,
      "description" -> text,
      "address" -> text,
      "workingHours" -> text,
      "loc" -> seq(of[Double])
    )(Restaurant.apply)(Restaurant.unapply)
  )

}
