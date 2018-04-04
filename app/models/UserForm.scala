package models

import play.api.data.Form
import play.api.data.Forms.{mapping, number, text, nonEmptyText, email}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

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

case class LoginForm(email:String,password:String)
case class Assignment(title: String,description: String)
case class UpdatePassword(email: String,newPassword: String, confirmPassword: String)

case class UpdateUserProfile(fname: String,mname: String,lname:String,age:Int)

class UserForm {

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

  val loginForm: Form[LoginForm] = Form(
    mapping(
      "email" -> text,
      "password" -> text.verifying(passwordCheckConstraint())
    )(LoginForm.apply)(LoginForm.unapply)
  )

  val updateProfile: Form[UpdateUserProfile] = Form(
    mapping(
      "fname" -> nonEmptyText,
      "mname" -> text,
      "lname" -> nonEmptyText,
      "age" -> number
    )(UpdateUserProfile.apply)(UpdateUserProfile.unapply)
  )


  val assignmentForm: Form[Assignment] = Form(
    mapping(
      "title" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Assignment.apply)(Assignment.unapply)
  )


  val updateForm = Form(
    mapping(
      "email" -> nonEmptyText,
      "newPassword" -> text.verifying(passwordCheckConstraint()),
      "confirmPassword" -> text.verifying(passwordCheckConstraint())
    )(UpdatePassword.apply)(UpdatePassword.unapply)
      .verifying("Password must match Confirm Password", verify => verify match {
        case user => passwordCheck(user.newPassword, user.confirmPassword)
      })
  )


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

  def passwordCheck(password:String,confirmPassword:String):Boolean = password == confirmPassword


}
