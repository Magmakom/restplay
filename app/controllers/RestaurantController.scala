package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import play.api._
import play.api.mvc._
import play.api.libs.json.Json

import repository.RestaurantRepository

class RestaurantController extends Controller {

  def findAllAsJson = Action.async { request =>
    RestaurantRepository.findAll().map { result =>
        Ok(Json.toJson(result))
    }
  }

}
