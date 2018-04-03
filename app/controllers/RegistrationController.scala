package controllers

import javax.inject.Inject

import models.{RegisterdUserInfo, UserInfoRepo}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.matching.Regex

case class UserData(
                     firstName: String,
                    middleName: String,
                    lastName: String,
                     email: String,
                    password: String,
                    confirmPassword: String,
                    mobileNumber: String,
                     gender:String,
                    age: Int)

class RegistrationController @Inject()(cc: ControllerComponents,userInfoRepository: UserInfoRepo) extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi

 // val nonEmptyAlphaText: Mapping[String] = nonEmptyText.verifying("Must Contain letter",name=>name.nonEmpty || name.matches("[A-za-z]"))

  val userForm: Form[UserData] = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "middleName" -> text,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "password" -> text.verifying(passwordCheckConstraint()),
      "confirmPassword" -> nonEmptyText,
      "mobileNumber" -> text.verifying(numberCheckConstraint()),
      "gender"  -> nonEmptyText,
      "age" -> number(min = 18, max=60),

    )(UserData.apply)(UserData.unapply)
        .verifying ("Password must match Confirm Password",verify =>verify match
        {case user => passwordCheck(user.password,user.confirmPassword)})
  )


  def passwordCheck(password:String,confirmPassword:String):Boolean = password == confirmPassword
  def register(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.registration(userForm))
  }



  def userPost(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] => {
    userForm.bindFromRequest.fold (
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


  val allNumbers = """\d*""".r
  val allLetters = """[A-Za-z]*""".r

  def passwordCheckConstraint(): Constraint[String] = {
    Constraint[String]("constraint.password") {
      plainText =>
        val errors = plainText match {
          case plaintext if plaintext.length < 8 => Seq(ValidationError("Password should be more than 8 characters"))
          case allNumbers() => Seq(ValidationError("Password is all numbers"))
          case allLetters() => Seq(ValidationError("Password is all letters"))
          case _ => Nil
        }
        if (errors.isEmpty) {
          Valid
        } else {
          Invalid(errors)
        }
    }
  }

  def numberCheckConstraint(): Constraint[String] = {

    Constraint[String]("constraint.mobile") {
      plainText =>
        val errors = plainText match {
          case plaintext if plaintext.length != 10 => Seq(ValidationError("Invalid number"))
          case allLetters() => Seq(ValidationError("Invalid number"))
          case _ => Nil
        }
        if (errors.isEmpty) {
          Valid
        } else {
          Invalid(errors)
        }
    }
  }

  }


