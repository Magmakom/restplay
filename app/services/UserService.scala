package services

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile

import models.User

/**
 * Handles actions to users.
 */
trait UserService extends IdentityService[User] {

}
