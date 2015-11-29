package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json

import repository.ReviewRepository
import  models.Review
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.api.Play.current
import play.api.i18n.Messages.Implicits._

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
      //
      // RestaurantRepository.findAll().map { result =>
      //     Ok(Json.toJson(result))
    // get the documents having this id (there will be 0 or 1 result)
    // val futureReview =
    ReviewRepository.findById("561a86597b1d8a1363b890a1").map  { result =>
    // ... so we get optionally the matching article, if any
    // let's use for-comprehensions to compose futures (see http://doc.akka.io/docs/akka/2.0.3/scala/futures.html#For_Comprehensions for more information)
    // for {
    //   // get a future option of article
    //   maybeReview <- futureReview
    //   // if there is some article, return a future of result with the article and its attachments
    //   result <- maybeReview.map { review =>
        // search for the matching attachments
        // find(...).toList returns a future list of documents (here, a future list of ReadFileEntry)
        // gridFS.find[JsObject, JSONReadFile](
        //   Json.obj("article" -> article.id.get)).collect[List]().map { files =>
        //   val filesWithId = files.map { file =>
        //     file.id -> file
        //   }
        //
        //   implicit val messages = messagesApi.preferred(request)
        result match {
          case None => Redirect(routes.Application.index)
          case Some(_) => Ok(views.html.editReview(Some(id), Review.form.fill(result.get)))
        }



        }
      }
        // }
//       }.getOrElse(Future(NotFound))
//     } yield result
//   }
// }


}
