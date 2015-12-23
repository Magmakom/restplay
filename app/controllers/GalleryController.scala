package controllers

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

import play.api.Logger
import play.api.Play.current
import play.api.i18n.MessagesApi
import play.api.mvc.{ Action, Controller, Request }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.BSONFormats._


import reactivemongo.api.gridfs.{ GridFS, ReadFile }

import play.modules.reactivemongo.{
  MongoController, ReactiveMongoApi, ReactiveMongoComponents
}

import play.modules.reactivemongo.json.collection._
import services.GalleryService

class GalleryController @Inject() (
  val messagesApi: MessagesApi,
  val reactiveMongoApi: ReactiveMongoApi)
    extends Controller with MongoController with ReactiveMongoComponents with GalleryService {

  import MongoController.readFileReads

  type JSONReadFile = ReadFile[JSONSerializationPack.type, JsString]
  val gridFS = GridFS(db, "photos")

  gridFS.ensureIndex().onComplete {
    case index =>
      Logger.info(s"Checked index, result is $index")
  }

  def getFilesWithId(id: String): Future[List[(play.api.libs.json.JsString, JSONReadFile)]] = {
    gridFS.find[JsObject, JSONReadFile](Json.obj("restaurantId" -> id))
      .collect[List]().map { files =>
        val filesWithId = files.map { file =>
            file.id -> file
        }
        filesWithId
      }
  }

  def getNameListAsJson(restaurantId: String) = Action.async {
    def filesCollection: JSONCollection = db.collection[JSONCollection]("photos.files")
    filesCollection.
      find(Json.obj("restaurantId" -> restaurantId), Json.obj("_id" -> 1)).
      cursor[JsObject].
      collect[List]().map {
        result =>  Ok(Json.toJson(result))
      }
  }

  def savePhoto(id: String) = Action.async(gridFSBodyParser(gridFS)) { request =>
      val futureFile = request.body.files.head.ref
      futureFile.onFailure {
        case err => err.printStackTrace()
      }
      val futureUpdate = for {
        file <- { println("_0"); futureFile }
        updateResult <- {
          println("_1")
          gridFS.files.update(
            Json.obj("_id" -> file.id),
            Json.obj("$set" -> Json.obj("restaurantId" -> id)))
        }
      } yield updateResult
      futureUpdate.map { _ =>
        Redirect(routes.RestaurantController.showEditForm(id))
      }.recover {
        case e => InternalServerError(e.getMessage())
      }
    }

  def getPhoto(id: String) = Action.async { request =>
    val file = gridFS.find[JsObject, JSONReadFile](Json.obj("_id" -> id))
    request.getQueryString("inline") match {
      case Some("true") =>
        serve[JsString, JSONReadFile](gridFS)(file, CONTENT_DISPOSITION_INLINE)
      case _ => serve[JsString, JSONReadFile](gridFS)(file)
    }
  }

  def removePhoto(id: String) = Action.async {
    import play.modules.reactivemongo.json.ImplicitBSONHandlers._

    gridFS.remove(Json toJson id).map(_ => Ok).
      recover { case _ => InternalServerError }
  }

}
