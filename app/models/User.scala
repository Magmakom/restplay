// package models
//
// case class User(
//   id: Option[String],
//   restaurantId: String,
//   cuisine: Int,
//   interior: Int,
//   service: Int,
//   resultMark: Option[Double],
//   title: String,
//   content: String,
//   publisher: String, // consider use an user object
//   creationDate: Option[DateTime],
//   updateDate: Option[DateTime])
//   {
//     resultMark match {
//       case None => def resultMark = (cuisine + interior + service)/3
//     }
//   }
//
//
// object User{
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
//   import reactivemongo.bson._
//
//   implicit object RestaurantWriter extends BSONDocumentWriter[Restaurant] {
//     def write(o: Restaurant): BSONDocument = BSONDocument(
//       if (o.id.isDefined) {
//         "_id" -> BSONObjectID(o.id.get)
//       } else {
//         "_id" -> BSONObjectID.generate
//       },
//       "name" -> o.name,
//       "telephone" -> o.telephone,
//       "description:" -> o.description,
//       "address" -> o.address,
//       "workingHours" -> o.workingHours,
//       "lat" -> o.positionLat,
//       "lng" -> o.positionLng)
//   }
//
//   implicit object RestaurantReader extends BSONDocumentReader[Restaurant] {
//     def read(doc: BSONDocument): Restaurant = {
//       val opt: Option[Restaurant] = for {
//         id <- doc.getAs[BSONObjectID]("_id").map(_.stringify)
//         restaurantId <- doc.getAs[BSONObjectID]("restaurantId").map(_.stringify)
//         cuisine <- doc.getAs[Int]("cuisine")
//         interior <- doc.getAs[Int]("interior")
//         service <- doc.getAs[Int]("service")
//         resultMark <- doc.getAs[Double]("resultMark").map(_.toDouble)
//         title <- doc.getAs[String]("title")
//         content <- doc.getAs[String]("content")
//         publisher <- doc.getAs[String]("publisher")
//         creationDate <- doc.getAs[BSONDateTime]("creationDate").map(dt => new DateTime(dt.value))
//         updateDate <- doc.getAs[BSONDateTime]("updateDate").map(dt => new DateTime(dt.value))
//       } yield new Restaurant( Some(id), name, telephone, description, address, workingHours, positionLat, positionLng )
//       opt.get
//     }
//   }
// }
