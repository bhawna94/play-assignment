package controllers

import javax.inject.Inject

import models.{UserForm, UserInfoRepo}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class LoginController@Inject()(
                               cc: ControllerComponents,userForms: UserForm, userInfoRepository: UserInfoRepo) extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi

  val nonEmptyAlphaText: Mapping[String] = nonEmptyText
    .verifying("Must Contain letter", name => name.nonEmpty || name.matches("[A-za-z]"))


  def login(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(userForms.loginForm))
  }

  def loginPost(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] => {
    userForms.loginForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.login(formWithErrors)))
      },
      user => {
        for {userCheck <- userInfoRepository.isUserCheck(user.email)
             userEnable <- userInfoRepository.isEnableCheck(user.email)
             validate <- userInfoRepository.validationOfUser(user.email, user.password)
        } yield {
          if (userCheck && userEnable && validate) {
            Redirect(routes.ProfileController.userProfile())
              .withSession("email" -> user.email)
          }
          else if (validate && !userCheck) {
            Redirect(routes.AdminController.adminProfile())
              .withSession("email" -> user.email)
          }
          else if (validate && !userEnable) {
            Redirect(routes.LoginController.login())
              .flashing("msg" -> "Your account has been disable by Admin")
          }
          else {
            Redirect(routes.LoginController.login())
              .flashing("msg" -> "Wrong Credentials")
          }
        }
      }
    )
  }
  }
}