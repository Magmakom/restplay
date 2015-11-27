package models


import org.joda.time.DateTime

case class Review(
  id: Option[String],
  restaurantId: String,
  cuisine: Int,
  interior: Int,
  service: Int,
  resultMark: Option[Double],
  title: String,
  content: String,
  creationDate: Option[DateTime],
  updateDate: Option[DateTime])
  {
    resultMark match {
      case None => def resultMark = (cuisine + interior + service)/3
    }
  }

object Review{
//
//   import play.api.libs.json._
//   import play.api.libs.functional.syntax._
//
//   implicit val restaurantWrites = new Writes[Restaurant] {
//     def writes(o: Restaurant) = Json.obj(
//       "id" -> o.id,
//       "name" -> o.name,
//       "telephone" -> o.telephone,
//       "description" -> o.description,
//       "address" -> o.address,
//       "workingHours" -> o.workingHours,
//       "lat" -> o.positionLat,
//       "lng" -> o.positionLng
//     )
//   }
//
  import reactivemongo.bson._

  // implicit object RestaurantWriter extends BSONDocumentWriter[Review] {
  //   def write(o: Review): BSONDocument = BSONDocument(
  //     if (o.id.isDefined) {
  //       "_id" -> BSONObjectID(o.id.get)
  //     } else {
  //       "_id" -> BSONObjectID.generate
  //     },
  //     "restaurantId" -> BSONObjectID(o.restaurantId),
  //     "cuisine" -> o.cuisine,
  //     "interior" -> o.interior,
  //     "service" -> o.service,
  //     "resultMark" -> o.resultMark,
  //     "title " -> o.title,
  //     "content" -> o.content,
  //     "creationDate" -> o.creationDate,
  //     "updateDate" -> o.updateDate)
  // }
  //
  // implicit object RestaurantReader extends BSONDocumentReader[Review] {
  //   def read(doc: BSONDocument): Review = {
  //     val opt: Option[Restaurant] = for {
  //       id <- doc.getAs[BSONObjectID]("_id").map(_.stringify)
  //       restaurantId <- doc.getAs[BSONObjectID]("restaurantId").map(_.stringify)
  //       cuisine <- doc.getAs[Int]("cuisine")
  //       interior <- doc.getAs[Int]("interior")
  //       service <- doc.getAs[Int]("service")
  //       resultMark <- doc.getAs[Double]("resultMark").map(_.toDouble)
  //       title <- doc.getAs[String]("title")
  //       content <- doc.getAs[String]("content")
  //       creationDate <- doc.getAs[BSONDateTime]("creationDate").map(dt => new DateTime(dt.value))
  //       updateDate <- doc.getAs[BSONDateTime]("updateDate").map(dt => new DateTime(dt.value))
  //     } yield new Restaurant( Some(id), name, telephone, description, address, workingHours, positionLat, positionLng )
  //     opt.get
  //   }
  // }

  import play.api.data._
  import play.api.data.format.Formats._
  import play.api.data.Forms._

  val reviewForm = Form(
    mapping(
      // "id" -> optional(of[String] verifying pattern(
      // """[a-fA-F0-9]{24}""".r,
      // "constraint.objectId",
      // "error.objectId")),
      "_id" -> optional(text),
      "restaurantId" -> nonEmptyText,
      "cuisine" -> number,
      "interior" -> number,
      "service" -> number,
      "resultMark" -> optional(of[Double]),
      "title " -> nonEmptyText,
      "content" -> text,
      "creationDate" -> optional(jodaDate),
      "updateDate" -> optional(jodaDate)
    )(Review.apply)(Review.unapply)
  )
}
