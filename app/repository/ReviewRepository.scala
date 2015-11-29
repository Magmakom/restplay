package repository

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._
import reactivemongo.api._
import reactivemongo.bson._
import org.joda.time.DateTime

import models.Review

object ReviewRepository extends ReviewRepository

trait ReviewRepository {

  def collection = MongoEnv.db("reviews")

  def findAll(): Future[List[Review]] ={
    collection.find(BSONDocument()).
      cursor[Review].
      collect[List](25)
  }

  def findById(id: String): Future[Option[Review]] = {
    collection.find(BSONDocument("_id" -> BSONObjectID(id))).
     one[Review]
  }

  def create(review: Review) = {
    collection.insert(review.copy(creationDate = Some(new DateTime()), updateDate = Some(new DateTime())))
  }

}
