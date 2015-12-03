package repository

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.bson.BSONObjectID
import reactivemongo.extensions.json.dao.JsonDao

import models.Review

object ReviewRepository
  extends JsonDao[Review, BSONObjectID](MongoEnv.db, "reviews") {

  def findById(id: String): Future[Option[Review]] = {
    ReviewRepository.findById(BSONObjectID(id))
  }


}
