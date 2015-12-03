package repository

import scala.concurrent.Future

import reactivemongo.bson.BSONObjectID
import reactivemongo.extensions.json.dao.JsonDao
import play.api.libs.concurrent.Execution.Implicits._
import play.modules.reactivemongo.json.BSONFormats._

import models.Restaurant

object RestaurantRepository
  extends JsonDao[Restaurant, BSONObjectID](MongoEnv.db, "restaurants"){

  def findById(id: String): Future[Option[Restaurant]] = {
    RestaurantRepository.findById(BSONObjectID(id))
  }

  // def getNameById(id:  String): Future[Option[String]] ={
  //
  // }

  // def findByName(name:  String): Future[List[Restaurant]] = {
  //   val query = BSONDocument(
  //     "name" -> BSONDocument(
  //       "$regexp" -> name))
  //   collection.find(query).
  //     cursor[Restaurant].
  //     collect[List](25)
  // }
}
