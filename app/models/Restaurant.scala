package models

case class Restaurant(
  id: Option[String],
  name: String,
  telephone: String,
  description: String,
  address: String,
  workingHours: String,
  positionLat: Double,
  positionLng: Double
)

object Restaurant{

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val restaurantWrites = new Writes[Restaurant] {
    def writes(o: Restaurant) = Json.obj(
      "id" -> o.id,
      "name" -> o.name,
      "telephone" -> o.telephone,
      "description" -> o.description,
      "address" -> o.address,
      "workingHours" -> o.workingHours,
      "lat" -> o.positionLat,
      "lng" -> o.positionLng
    )
  }

  import reactivemongo.bson._

  implicit object RestaurantWriter extends BSONDocumentWriter[Restaurant] {
    def write(o: Restaurant): BSONDocument = BSONDocument(
      if (o.id.isDefined) {
        "_id" -> BSONObjectID(o.id.get)
      } else {
        "_id" -> BSONObjectID.generate
      },
      "name" -> o.name,
      "telephone" -> o.telephone,
      "description:" -> o.description,
      "address" -> o.address,
      "workingHours" -> o.workingHours,
      "lat" -> o.positionLat,
      "lng" -> o.positionLng
    )
  }

  implicit object RestaurantReader extends BSONDocumentReader[Restaurant] {
    def read(doc: BSONDocument): Restaurant = {
      val opt: Option[Restaurant] = for {
        id <- doc.getAs[BSONObjectID]("_id").map(_.stringify)
        name <- doc.getAs[String]("name")
        telephone <- doc.getAs[String]("telephone")
        description <- doc.getAs[String]("description")
        address <- doc.getAs[String]("address")
        workingHours <- doc.getAs[String]("workingHours")
        positionLat <- doc.getAs[Double]("lat").map(_.toDouble)
        positionLng <- doc.getAs[Double]("lng").map(_.toDouble)
      } yield new Restaurant( Some(id), name, telephone, description, address, workingHours, positionLat, positionLng )
      opt.get
    }
  }
}
