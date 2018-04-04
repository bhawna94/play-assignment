package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import models.{UserForm, UserInfoRepo}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ForgotPasswordController@Inject()(cc: ControllerComponents,userForms:UserForm,userInfoRepository: UserInfoRepo)
                                       extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi



  def viewForm(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.forgot(userForms.updateForm))
  }

  def updatePassword(): Action[AnyContent] = Action.async { implicit request =>
    userForms.updateForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.forgot(formWithErrors)))
      },
      userData => {
        userInfoRepository.isUserRegistered(userData.email)
          .map {
            case false => Redirect(routes.HomeController.index())
              .flashing("msg" -> "You are not a registered user!!!!")
            case true => userInfoRepository.updatePassword(userData.email, userData.newPassword)
              Redirect(routes.LoginController.login())
                .flashing("msg" -> "you can login with new credentials")
          }
      }
    )
  }

}
