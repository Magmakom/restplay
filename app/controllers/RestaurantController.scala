package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json

import repository.RestaurantRepository
import scala.concurrent.ExecutionContext.Implicits.global


class RestaurantController extends Controller {

  // def findAll = Action.async {
  //   Ok(views.html.index())
  // }

  def findAllAsJson = Action.async { request =>
    RestaurantRepository.findAll().map { result =>
        Ok(Json.toJson(result))
  }
}

}
