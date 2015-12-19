package models

import org.joda.time.DateTime
import reactivemongo.bson.BSONObjectID
import scala.collection.Seq

sealed case class Label(
  text: String,
  color: String,
  IsPositive: Boolean
)

case class Review(
  _id: BSONObjectID = BSONObjectID.generate,
  restaurantId: BSONObjectID,
  cuisine: Int,
  interior: Int,
  service: Int,
  resultMark: Double,
  title: String,
  content: String,
  creationDate: DateTime = new DateTime,
  updateDate: DateTime = new DateTime,
  labels: Option[Seq[Label]]
)

object Review{

  import play.api.libs.json.Json
  import play.modules.reactivemongo.json.BSONFormats._

  implicit val labelFormat = Json.format[Label]
  implicit val reviewFormat = Json.format[Review]

  import play.api.data._
  import play.api.data.format.Formats._
  import play.api.data.Forms._
  import helpers.BSONObjectIDHelper.BSONObjectIDFormFormat

  val form = Form(
    mapping(
      "id" -> of[BSONObjectID],
      "restaurantId" -> of[BSONObjectID],
      "cuisine" -> number,
      "interior" -> number,
      "service" -> number,
      "resultMark" -> of[Double],
      "title" -> text,
      "content" -> text,
      "creationDate" -> jodaDate,
      "updateDate" -> jodaDate,
      "labels" -> optional(seq(mapping(
        "text" -> text,
        "color" -> text,
        "IsPositive" -> boolean
      )(Label.apply)(Label.unapply)))
    )(Review.apply)(Review.unapply)
  )

}
