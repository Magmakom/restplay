package repository

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._
import play.modules.reactivemongo.json.BSONFormats._
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
import reactivemongo.extensions.json.dao.JsonDao

import models.Review

object ReviewRepository
  extends JsonDao[Review, BSONObjectID](MongoEnv.db, "reviews") {

  def findById(id: String): Future[Option[Review]] = {
    findById(BSONObjectID(id))
  }

  def findLatestReview(restaurantId: String): Future[Option[Review]] = {
    import play.modules.reactivemongo.json._
    ReviewRepository.collection.
      find(Json.obj("restaurantId" -> BSONObjectID(restaurantId))).
      // find(Json.obj("title" ->"Lorem Ipsum")).
      sort(Json.obj("updateDate" -> -1)).
      one[Review]
  }


}
