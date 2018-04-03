package controllers

import javax.inject.Inject

import models.UserInfoRepo
import play.api.data.Forms._
import play.api.data.{Form, Mapping}
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
case class LoginForm(email:String,password:String)
class LoginController@Inject()(registrationController: RegistrationController,
                               cc: ControllerComponents,userInfoRepository: UserInfoRepo) extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi

  val nonEmptyAlphaText: Mapping[String] = nonEmptyText
    .verifying("Must Contain letter",name=>name.nonEmpty || name.matches("[A-za-z]"))

  val loginForm: Form[LoginForm] = Form(
    mapping(
      "email" -> text,
      "password" -> text.verifying(registrationController.passwordCheckConstraint())
    )(LoginForm.apply)(LoginForm.unapply)
  )

  def login(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(loginForm))
  }
  def loginPost(): Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] => {
    loginForm.bindFromRequest.fold (
      formWithErrors => {
        Future.successful(BadRequest(views.html.login(formWithErrors))) },
      user => { userInfoRepository.validationOfUser(user.email,user.password).map{
        case true =>
          val userCheck = Await
            .result(userInfoRepository.isUserCheck(user.email), Duration.Inf)
          val userEnable = Await
            .result(userInfoRepository.isEnableCheck(user.email), Duration.Inf)

          if (userCheck && userEnable) {
            Redirect(routes.ProfileController.userProfile())
              .withSession("email" -> user.email)
          }
          else if (!userCheck) {
            Redirect(routes.AdminController.adminProfile())
              .withSession("email" -> user.email)
          }
          else if (!userEnable) {
            Redirect(routes.LoginController.login())
              .flashing("msg" -> "Your account has been disable by Admin")
          }
          else {
            Redirect(routes.HomeController.index())
          }
        case false =>Redirect(routes.LoginController.login())
          .flashing("msg"->"Wrong Credentials")
      }
      }
    )
  }
  }
}
