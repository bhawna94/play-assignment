package controllers

import javax.inject.Inject

import models.{RegisterdUserInfo, UserForm, UserInfoRepo}
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegistrationController @Inject()(cc: ControllerComponents,
                                       userForms: UserForm, userInfoRepository: UserInfoRepo) extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi
  def register(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.registration(userForms.userForm))
  }

  def userPost(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] => {
    userForms.userForm.bindFromRequest.fold (
      formWithErrors => {
        Future.successful(BadRequest(views.html.registration(formWithErrors))) },
      userData => {userInfoRepository.isUserRegistered(userData.email)
          .map{
        case true => Redirect(routes.LoginController.login()).flashing("msg" -> "You are already registered!!!!")
        case false =>userInfoRepository.store(RegisterdUserInfo(1,
          userData.firstName,
          userData.middleName,
          userData.lastName,
          userData.email,
          userData.password,
          userData.mobileNumber,
          userData.gender,
          userData.age,
          true,
          true))
          Redirect(routes.ProfileController.userProfile).withSession("email" -> userData.email).flashing("msg" -> "You are registered successfully")
      }
      })}
  }
}




