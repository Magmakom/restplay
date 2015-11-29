package repository

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._
import reactivemongo.api._
import reactivemongo.bson._

import models.Restaurant

object RestaurantRepository extends RestaurantRepository

trait RestaurantRepository {

  def collection = MongoEnv.db("restaurants")

  def findAll(): Future[List[Restaurant]] ={
    collection.find(BSONDocument()).
      cursor[Restaurant].
      collect[List](25)
  }

  def findById(id: String): Future[Option[Restaurant]] = {
    collection.find(BSONDocument("_id" -> BSONObjectID(id))).
     one[Restaurant]
  }

  // def getNameById(id:  String): Future[Option[String]] ={
  //
  // }

  def findByName(name:  String): Future[List[Restaurant]] = {
    val query = BSONDocument(
      "name" -> BSONDocument(
        "$regexp" -> name))
    collection.find(query).
      cursor[Restaurant].
      collect[List](25)
  }
}
