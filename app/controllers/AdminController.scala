package controllers

import javax.inject.Inject

import models.{AssignmentInfo, AssignmentInfoRepo, UserForm, UserInfoRepo}
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, text}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class AdminController  @Inject()(cc: ControllerComponents,userForms:UserForm, assignmentInfoRepo:AssignmentInfoRepo,userInfoRepo: UserInfoRepo)
  extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi




  def adminProfile(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.adminprofile())
  }

  def displayAssignmentForm(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.assignment(userForms.assignmentForm))
  }

  def addAssignment(): Action[AnyContent] = Action.async { implicit request =>
    userForms.assignmentForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.assignment(formWithErrors)))
      },
      assignments => {
        assignmentInfoRepo.storeAssignment(AssignmentInfo(0, assignments.title, assignments.description))
          .map {
          case false => Redirect(routes.HomeController.index())
          case true => Redirect(routes.AdminController.displayAssignmentForm())
            .flashing("success" -> "Assignment is added successfully!")
        }


      }
    )
  }

 def viewAssignment(): Action[AnyContent] = Action.async { implicit request  =>
  val listOfAssignment = assignmentInfoRepo.getAssignment()
   listOfAssignment.map(assignment => Ok(views.html.adminviewassignment(assignment)))

 }

  def deleteAssignment(id: Int): Action[AnyContent] = Action.async{
    assignmentInfoRepo.deleteAssignment(id).map {
      case true => Redirect(routes.AdminController.viewAssignment())
        .flashing("success"->"Successfully Deleted")
      case false => Redirect(routes.AdminController.viewAssignment())
        .flashing("failure" -> "Already deleted from Database")
    }
  }

  def viewUser(): Action[AnyContent] = Action.async { implicit request =>
    val listOfUser = userInfoRepo.getUser()
    listOfUser.map(user => Ok(views.html.adminviewuser(user)))
  }

  def enableUser(emailId:String): Action[AnyContent] = Action.async { implicit request =>
    userInfoRepo.enable(emailId).map {
      case true=>Redirect(routes.AdminController.viewUser())
        .flashing("msg" -> "Successfully Enable")
      case false=>Redirect(routes.AdminController.viewUser())
        .flashing("msg" -> "fail")
    }
  }

  def disableUser(emailId: String): Action[AnyContent] = Action.async { implicit request =>
    userInfoRepo.disable(emailId).map {
      case true=>Redirect(routes.AdminController.viewUser())
        .flashing("msg" -> "Successfully Disable")
      case false=>Redirect(routes.AdminController.viewUser())
        .flashing("msg"-> "Fail")
    }
  }
}
