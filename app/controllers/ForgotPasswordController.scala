package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import models.UserInfoRepo
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class UpdatePassword(email: String,newPassword: String, confirmPassword: String)
class ForgotPasswordController@Inject()(registrationController: RegistrationController,
                                        cc: ControllerComponents,userInfoRepository: UserInfoRepo) extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi


  val updateForm = Form(
    mapping(
      "email" -> nonEmptyText,
      "newPassword" -> text.verifying(registrationController.passwordCheckConstraint()),
      "confirmPassword" -> text.verifying(registrationController.passwordCheckConstraint())
    )(UpdatePassword.apply)(UpdatePassword.unapply)
      .verifying("Password must match Confirm Password", verify => verify match {
        case user => registrationController.passwordCheck(user.newPassword, user.confirmPassword)
      })
  )

  def viewForm(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.forgot(updateForm))
  }

  def updatePassword(): Action[AnyContent] = Action.async { implicit request =>
    updateForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.forgot(formWithErrors)))
      },
      userData => {
        userInfoRepository.isUserRegistered(userData.email)
          .map {
            case false => Redirect(routes.HomeController.index()).flashing("msg" -> "You are not a registered user!!!!")
            case true => userInfoRepository.updatePassword(userData.email, userData.newPassword)
              Redirect(routes.LoginController.login()).flashing("msg" -> "you can login with new credentials")
          }
      }
    )
  }

}
