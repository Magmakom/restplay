package repository

import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._

object MongoEnv {
  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  val db = connection("restplay")
}
