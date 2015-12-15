package controllers

import models.Restaurant
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json._
import play.api.mvc._
import repository.RestaurantRepository

import scala.concurrent.ExecutionContext.Implicits.global

class RestaurantController extends Controller {

  val jsonTransformer = (__).json.update( (__ \ '_id).json.copyFrom( (__ \ '_id \ '$oid).json.pick ) ) //'

  def findAllAsJson = Action.async { request =>
    RestaurantRepository.findAll().map { result =>
      Json.toJson(result) match {
        case JsArray(elements) => {
          val out = elements.map(_.transform(jsonTransformer).get)
          Ok(Json.toJson(out))
        }
        case _ => sys.error("Oops! JsArray expected.")
      }
    }
  }

  def showCreationForm = Action { request =>
    Ok(views.html.editRestaurant(None, Restaurant.form))
  }

  def showEditForm(id: String) = Action.async { request =>
    RestaurantRepository.findById(id).map  { result =>
      result match {
        case None => Redirect(routes.Application.index)
        case Some(_) => Ok(views.html.editRestaurant(Some(id), Restaurant.form.fill(result.get)))
      }
    }
  }

}
