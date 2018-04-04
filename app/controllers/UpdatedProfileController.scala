package controllers

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import models.{UpdateProfile, UpdateUserProfile, UserForm, UserInfoRepo}
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.Future

class UpdatedProfileController@Inject()(cc: ControllerComponents,userForms:UserForm,userInfoRepository: UserInfoRepo)
  extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi

  def profile(): Action[AnyContent] =  Action { implicit request => {
    Ok(views.html.updateprofile(userForms.updateProfile))
  }
  }

  def getProfileDetails(): Action[AnyContent] = Action.async { implicit request =>
    val email = request.session.get("email").get
    val userDetails = userInfoRepository.getUserDetails(email)
    userDetails.map {
      data =>
        Ok(views.html.updateprofile(userForms.updateProfile
          .fill(UpdateUserProfile(data.fname,data.mname,data.lname,data.age))))
    }
  }

  def updateDetails(): Action[AnyContent] = Action.async { implicit request =>
    userForms.updateProfile.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.updateprofile(formWithErrors)))
      },
      profile => {
        userInfoRepository.updateUserDetails(request.session.get("email")
          .get,UpdateProfile(profile.fname, profile.mname, profile.lname,
          profile.age) ).map {
          case true =>
            Redirect(routes.UpdatedProfileController.getProfileDetails())
          case false => Redirect(routes.HomeController.index())
        }
      })
  }

  }


