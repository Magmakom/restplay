package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import play.api._
import play.api.mvc._
import play.api.libs.json._

import repository.RestaurantRepository

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

}
