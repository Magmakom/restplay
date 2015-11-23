package repository

import models.Restaurant
import scala.concurrent.Future
import reactivemongo.api._
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONObjectID
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

object RestaurantRepository extends RestaurantRepository

trait RestaurantRepository {
  def collection = MongoEnv.db("restaurants")

  def findAll(): Future[List[Restaurant]] ={
    collection.find(BSONDocument()).
      cursor[Restaurant].
      collect[List]()
  }

  // def findById(id: String): Future[Restaurant] = {
  //   collection.find(BSONDocument("_id" -> BSONObjectID(id))).
  //     cursor[Restaurant]
  // }

}
