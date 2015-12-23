package services

import scala.concurrent.Future


trait GalleryService {

def getFilesWithId(id: String): Future[List[(play.api.libs.json.JsString, reactivemongo.api.gridfs.ReadFile[play.modules.reactivemongo.json.JSONSerializationPack.type, play.api.libs.json.JsString])]]
}
