package helpers

import play.api.data.FormError
import play.api.data.format._
import reactivemongo.bson.BSONObjectID

object  BSONObjectIDHelper {

  // see https://groups.google.com/forum/#!topic/reactivemongo/IJLh8GqCyRY
  // TODO clean up
  implicit  object BSONObjectIDFormFormat extends Formatter[BSONObjectID] {

    private def strFormat: Formatter[String] = new Formatter[String] {
      def bind(key: String, data: Map[String, String]) = data.get(key).toRight(Seq(FormError(key, "error.required", Nil)))
      def unbind(key: String, value: String) = Map(key -> value)
    }

    private def parsing[T](parse: String => T, errMsg: String, errArgs: Seq[Any])(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
      strFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[T]
          .either(parse(s))
          .left.map(e => Seq(FormError(key, errMsg, errArgs)))
      }
    }

    def bind(key: String, data: Map[String, String]) = parsing(BSONObjectID(_), "error.bsonObjectId", Nil)(key, data)
    def unbind(key: String, value: BSONObjectID) = Map(key -> value.stringify)
  }

}
