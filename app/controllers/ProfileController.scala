package controllers

import javax.inject.Inject

import models.{AssignmentInfo, AssignmentInfoRepo, UserInfoRepo}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class ProfileController  @Inject()(cc: ControllerComponents
                                   ,assignmentInfoRepo:AssignmentInfoRepo,userInfoRepo: UserInfoRepo) extends AbstractController(cc) with I18nSupport {
  implicit val message = cc.messagesApi
  def userProfile(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.userprofilepage())
  }

  def viewAssignmentByUser(): Action[AnyContent] = Action.async { implicit request =>
    val listOfAssignment: List[AssignmentInfo] = Await.result(assignmentInfoRepo.getAssignment(),Duration.Inf)
    Future.successful(Ok(views.html.userviewassignment(listOfAssignment)))
  }
}
