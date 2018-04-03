package controllers

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import models.{UpdateProfile, UserInfoRepo}
import play.api.data.Form
import play.api.data.Forms.{email, mapping, nonEmptyText, number,text}
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.Future
case class UpdateUserProfile(fname: String,mname: String,lname:String,age:Int)
class UpdatedProfileController@Inject()(cc: ControllerComponents,userInfoRepository: UserInfoRepo) extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi
  val updateForm: Form[UpdateUserProfile] = Form(
    mapping(
      "fname" -> nonEmptyText,
      "mname" -> text,
      "lname" -> nonEmptyText,
      "age" -> number
    )(UpdateUserProfile.apply)(UpdateUserProfile.unapply)
  )

  def profile(): Action[AnyContent] =  Action { implicit request => {
    Ok(views.html.updateprofile(updateForm))
  }
  }

  def getProfileDetails(): Action[AnyContent] = Action.async { implicit request =>
    val email = request.session.get("email").get
    val userDetails = userInfoRepository.getUserDetails(email)
    userDetails.map {
      data =>
        Ok(views.html.updateprofile(updateForm.fill(UpdateUserProfile(data.fname,data.mname,data.lname,data.age))))
    }
  }

  def updateDetails(): Action[AnyContent] = Action.async { implicit request =>
    updateForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.updateprofile(formWithErrors)))
      },
      profile => {
        userInfoRepository.updateUserDetails(request.session.get("email").get,UpdateProfile(profile.fname, profile.mname, profile.lname,
          profile.age) ).map {
          case true =>
            Redirect(routes.UpdatedProfileController.getProfileDetails())
          case false => Redirect(routes.HomeController.index())
        }
      })
  }

  }


