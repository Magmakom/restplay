package repository

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.bson.BSONObjectID
import reactivemongo.extensions.json.dao.JsonDao
import reactivemongo.extensions.json.dsl.JsonDsl._

import models.Restaurant

object RestaurantRepository
  extends JsonDao[Restaurant, BSONObjectID](MongoEnv.db, "restaurants"){

  def findById(id: String): Future[Option[Restaurant]] = {
    findById(BSONObjectID(id))
  }

  def findByName(name: String): Future[List[Restaurant]] = {
      findAll(selector = "name" $regex (name, "i"))
  }


}
