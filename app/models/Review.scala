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
  updateDate: Option[DateTime]
)

object Review{

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val reviewWrites = new Writes[Review] {
    def writes(o: Review) = Json.obj(
      "id" -> o.id,
      "restaurantId" -> o.restaurantId,
      "cuisine" -> o.cuisine,
      "interior" -> o.interior,
      "service" -> o.service,
      "resultMark" -> o.resultMark,
      "title" -> o.title,
      "content" -> o.content,
      "creationDate" -> o.creationDate,
      "updateDate" -> o.updateDate
    )
  }

  implicit  val reviewReads = new Reads[Review] {
    def reads(json: JsValue): JsResult[Review] = json match {
      case obj: JsObject => try {
        val id = (obj \ "_id").asOpt[String]
        val restaurantId = (obj \ "restaurantId").as[String]
        val cuisine = (obj \ "cuisine").as[Int]
        val interior = (obj \ "interior").as[Int]
        val service = (obj \ "service").as[Int]
        val resultMark = (obj \ "resultMark").asOpt[Double]
        val title = (obj \ "title").as[String]
        val content = (obj \ "content").as[String]
        val creationDate = (obj \ "creationDate").asOpt[Long]
        val updateDate = (obj \ "updateDate").asOpt[Long]
        JsSuccess(Review(id, restaurantId, cuisine, interior, service, resultMark, title, content,
          creationDate.map(new DateTime(_)),
          updateDate.map(new DateTime(_))))
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }

  import reactivemongo.bson._

  implicit object ReviewWriter extends BSONDocumentWriter[Review] {
    def write(o: Review): BSONDocument = BSONDocument(
      if (o.id.isDefined) {
        "_id" -> BSONObjectID(o.id.get)
      } else {
        "_id" -> BSONObjectID.generate
      },
      "restaurantId" -> BSONObjectID(o.restaurantId),
      "cuisine" -> o.cuisine,
      "interior" -> o.interior,
      "service" -> o.service,
      "resultMark" -> o.resultMark,
      "title " -> o.title,
      "content" -> o.content,
      "creationDate" -> o.creationDate.map(date => BSONDateTime(date.getMillis)),
      "updateDate" -> o.updateDate.map(date => BSONDateTime(date.getMillis))
    )
  }

  implicit object  ReviewReader extends BSONDocumentReader[Review] {
      def read(doc: BSONDocument): Review =
        new Review(
          doc.getAs[BSONObjectID]("_id").map(_.stringify),
          doc.getAs[BSONObjectID]("restaurantId").map(_.stringify).get,
          doc.getAs[Int]("cuisine").get,
          doc.getAs[Int]("interior").get,
          doc.getAs[Int]("service").get,
          doc.getAs[Double]("resultMark").map(_.toDouble),
          doc.getAs[String]("title").get,
          doc.getAs[String]("content").get,
          doc.getAs[BSONNumberLike]("creationDate").map(dt => new DateTime(dt.toLong)),
          doc.getAs[BSONNumberLike]("updateDate").map(dt => new DateTime(dt.toLong))
        )
    }

  import play.api.data._
  import play.api.data.format.Formats._
  import play.api.data.Forms._

  val form = Form(
    mapping(
      "id" -> optional(text),
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
