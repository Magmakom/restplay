package repository

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.inject.ApplicationLifecycle
import play.api.Play.current
import scala.concurrent.Future
import scala.util.{ Success, Failure }
import reactivemongo.api._

object MongoEnv {

  lazy val db = {
    val uri = current.configuration.getString("mongodb.uri")
    val parsedUri: MongoConnection.ParsedURI =
      MongoConnection.parseURI(uri.get) match {
        case Success(parsedURI) => parsedURI
        case Failure(e) => sys error s"Invalid mongodb.uri"
      }
    val driver = new MongoDriver
    val connection = driver.connection(parsedUri)

    parsedUri.db.fold[DefaultDB](sys error s"cannot resolve database from URI: $parsedUri") { dbUri =>
      val db = DB(dbUri, connection)
      registerDriverShutdownHook(driver)
      //loginfo(s"""ReactiveMongoApi successfully started with DB '$dbUri'! Servers:\n\t\t${parsedUri.hosts.map { s => s"[${s._1}:${s._2}]" }.mkString("\n\t\t")}""")
      db
    }
  }

  // def apply(name: String): BSONCollection = db(name)

  private def registerDriverShutdownHook(mongoDriver: MongoDriver): MongoDriver = {
    current.injector.instanceOf[ApplicationLifecycle].
      addStopHook { () => Future(mongoDriver.close()) }
    mongoDriver
  }
}
