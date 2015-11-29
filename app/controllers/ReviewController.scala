package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import models.Review
import repository.ReviewRepository

class ReviewController extends Controller {

  def findAllAsJson = Action.async { request =>
    ReviewRepository.findAll().map { result =>
      Ok(Json.toJson(result))
    }
  }

  def showCreationForm = Action { request =>
    Ok(views.html.editReview(None, Review.form))
  }

  def showEditForm(id: String) = Action.async { request =>
    ReviewRepository.findById(id).map  { result =>
      result match {
        case None => Redirect(routes.Application.index)
        case Some(_) => Ok(views.html.editReview(Some(id), Review.form.fill(result.get)))
      }
    }
  }

}
