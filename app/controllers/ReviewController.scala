package controllers

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.Play.current
import play.api.i18n.MessagesApi
import com.mohiva.play.silhouette.api.{ Environment, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import org.joda.time.DateTime

import models.{ Review, User }
import repository.ReviewRepository

class ReviewController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator])
  extends Silhouette[User, CookieAuthenticator] {

  def findAllAsJson = Action.async { request =>
    ReviewRepository.findAll().map { result =>
      Ok(Json.toJson(result))
    }
  }

  def findByIdAsJson(id: String) = Action.async { request =>
    ReviewRepository.findById(id).map  { result =>
      Ok(Json.toJson(result))
    }
  }

  def create = SecuredAction.async { implicit request =>
    Review.form.bindFromRequest.fold(
      errors => Future.successful(
        Ok(views.html.editReview("", errors))),
      // if no error, then insert the article into the 'reviews' collection
      review => ReviewRepository.insert(review.copy())
    ).map(_ => Redirect(routes.Application.index))
  }

  def edit(id: String) = SecuredAction.async { implicit request =>
    import reactivemongo.bson.BSONDateTime
    import play.modules.reactivemongo.json.BSONFormats._
    import play.modules.reactivemongo.json._

    Review.form.bindFromRequest.fold(
      errors => Future.successful(
        Ok(views.html.editReview(id, errors))),
      review => {
        val modifier = Json.obj(
          "$set" -> Json.obj(
            // "updateDate" -> BSONDateTime(new DateTime().getMillis),
            "updateDate" -> review.updateDate,
            "title" -> review.title,
            "content" -> review.content,
            "cuisine" -> review.cuisine,
            "interior" -> review.interior,
            "service" -> review.service,
            "resultMark" -> review.resultMark,
            "labels" -> review.labels))

        // ok, let's do the update
        ReviewRepository.updateById(review._id, modifier).
          map { _ => Redirect(routes.Application.index) }
      })
  }

  def delete(id: String) = SecuredAction.async {
    import reactivemongo.bson.BSONObjectID
    ReviewRepository.removeById(BSONObjectID(id)).map(_ => Ok).recover { case _ => InternalServerError }
  }

  def showCreationForm(restaurantId: String) = SecuredAction.async  { request =>
    Future.successful(Ok(views.html.createReview(Review.form, restaurantId)))
  }

  def showEditForm(id: String) = SecuredAction.async  { request =>
    ReviewRepository.findById(id).map  { result =>
      result match {
        case None => Redirect(routes.Application.index)
        case Some(_) => Ok(views.html.editReview(id, Review.form.fill(result.get)))
      }
    }
  }

}
