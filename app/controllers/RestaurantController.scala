package controllers

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.Play.current
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.mvc._
import com.mohiva.play.silhouette.api.{ Environment, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

import models.{ User, Restaurant }
import repository.RestaurantRepository
import services.GalleryService

class RestaurantController @Inject() (
  val messagesApi: MessagesApi,
  val galleryService: GalleryService,
  val env: Environment[User, CookieAuthenticator])
  extends Silhouette[User, CookieAuthenticator] {

  private val jsonTransformer = (__).json.update( (__ \ '_id).json.copyFrom( (__ \ '_id \ '$oid).json.pick ) ) //'

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

  def findByIdAsJson(id: String) = Action.async { request =>
    RestaurantRepository.findById(id).map  { result =>
      Ok(Json.toJson(result).transform(jsonTransformer).get)
    }
  }

  def create = SecuredAction.async { implicit request =>
    import reactivemongo.bson.BSONObjectID

    Restaurant.form.bindFromRequest.fold(
      errors => Future.successful(
        Ok(views.html.createRestaurant(errors, 0 , 0))),
      restaurant => RestaurantRepository.insert(restaurant.copy(
        _id = BSONObjectID.generate
      ))
    ).map(_ => Redirect(routes.Application.index))
  }

  def edit(id: String) = SecuredAction.async { implicit request =>
    import play.modules.reactivemongo.json.BSONFormats._
    import play.modules.reactivemongo.json._
    import reactivemongo.bson.BSONObjectID

    Restaurant.form.bindFromRequest.fold(
      errors => Future.successful(
        Ok(views.html.editRestaurant(id, errors))),
      restraurant => {
        val modifier = Json.obj(
          "$set" -> Json.obj(
            "name" -> restraurant.name,
            "telephone" -> restraurant.telephone,
            "description" -> restraurant.description,
            "address" -> restraurant.address,
            "workingHours" -> restraurant.workingHours))
        RestaurantRepository.updateById(BSONObjectID(id), modifier).map { _ =>
          Redirect(routes.Application.index)
        }
      }
    )
  }

  def delete(id: String) = SecuredAction.async {
    import reactivemongo.bson.BSONObjectID
    RestaurantRepository.removeById(BSONObjectID(id)).map(_ => Ok).recover { case _ => InternalServerError }
  }

  def showCreationForm(lat: Double, lng: Double) = SecuredAction.async  { request =>
    Future.successful(Ok(views.html.createRestaurant(Restaurant.form, lat, lng)))
  }

  def showEditForm(id: String) = SecuredAction.async { request =>
    // RestaurantRepository.findById(id).map  { result =>
    //   result match {
    //     case None => Redirect(routes.Application.index)
    //     case Some(_) => {
    //       galleryService.getFilesWithId(id).map {
    //         case filesWithId => Ok(views.html.editRestaurant(id, Restaurant.form.fill(result.get), Some(filesWithId)))
    //       }
    //       Ok(views.html.editRestaurant(id, Restaurant.form.fill(result.get)))
    //     }
    //   }
    // }
    for {
      futRestaurant <- RestaurantRepository.findById(id)
      futFiles <- galleryService.getFilesWithId(id)
    } yield Ok(views.html.editRestaurant(id, Restaurant.form.fill(futRestaurant.get), Some(futFiles)))
  }

}
