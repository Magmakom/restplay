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
    println("Hi! I'm a little review")
  }

object Review{

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val restaurantWrites = new Writes[Review] {
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

  import reactivemongo.bson._

  // implicit object ReviewWriter extends BSONDocumentWriter[Review] {
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

  implicit object  ReviewReader extends BSONDocumentReader[Review] {
    // def read(doc: BSONDocument): Review = {
    //   val opt: Option[Review] = for {
    //     id <- doc.getAs[BSONObjectID]("_id").map(_.stringify)
    //     restaurantId <- doc.getAs[BSONObjectID]("restaurantId").map(_.stringify)
    //     cuisine <- doc.getAs[BSONNumberLike]("cuisine").map(_.toInt)
    //     interior <- doc.getAs[BSONNumberLike]("interior").map(_.toInt)
    //     service <- doc.getAs[BSONNumberLike]("service").map(_.toInt)
    //     resultMark <- doc.getAs[Double]("resultMark").map(_.toDouble)
    //     title <- doc.getAs[String]("title")
    //     content <- doc.getAs[String]("content")
    //     creationDate <- doc.getAs[BSONDateTime]("creationDate").map(dt => new DateTime(dt.value))
    //     updateDate <- doc.getAs[BSONDateTime]("updateDate").map(dt => new DateTime(dt.value))
    //   } yield new Review( Some(id), restaurantId, cuisine, interior, service, Some(resultMark), title, content, Some(creationDate), Some(updateDate)
    //   )
    //   opt match {
    //     case Some(_) => opt.get
    //     case None => {
    //       println("ALLO YOBA PSHHHH PSHHHHHHHHHH")
    //       opt.get
    //     }
    //   }
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
      // "id" -> optional(of[String] verifying pattern(
      // """[a-fA-F0-9]{24}""".r,
      // "constraint.objectId",
      // "error.objectId")),
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
