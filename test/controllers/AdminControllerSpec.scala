package controllers

import models._
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, status, stubControllerComponents}
import akka.util.Timeout
import play.api.test.CSRFTokenHelper._

import scala.concurrent.duration._
import scala.concurrent.Future

class AdminControllerSpec extends PlaySpec with Mockito {

  implicit val timeout = Timeout(20 seconds)
  "go to user profile page" in {
    val controller = getMockedObject
    val result = controller.adminController.adminProfile().apply(FakeRequest())
    status(result) must equal (OK)
  }


  "view Assignment by Admin" in {
    val controller = getMockedObject
    val assignment = List(AssignmentInfo(1,"Play","registration"))

    when(controller.mockedAssignmentInfoRepository.getAssignment()) thenReturn Future.successful(assignment)
    val result = controller.adminController.viewAssignment().apply(FakeRequest())
    status(result) must equal (OK)
  }


  "display Assignment" in {
    val controller = getMockedObject
    when(controller.mockedUserForm.assignmentForm) thenReturn {val form = new UserForm{}
    form.assignmentForm}
    val result = controller.adminController.displayAssignmentForm().apply(FakeRequest()
      .withCSRFToken)
    status(result) must equal(OK)
  }



  "view User" in {
    val controller = getMockedObject
    val userList = List(RegisterdUserInfo(1, "bhawna", "", "Sharma", "94sharma@gmail",
      "123456abcdef", "9818251174", "Female", 20,
      true, false))

    when(controller.mockedUserInfoRepository.getUser()) thenReturn Future.successful(userList)
    val result = controller.adminController.viewUser().apply(FakeRequest())
    status(result) must equal (OK)
  }


  def getMockedObject: TestObj = {

    val mockedUserInfoRepository = mock[UserInfoRepo]
    val mockedAssignmentInfoRepository = mock[AssignmentInfoRepo]
    val mockedUserForm = mock[UserForm]

    val controller = new AdminController(stubControllerComponents(),mockedUserForm,mockedAssignmentInfoRepository,mockedUserInfoRepository)

    TestObj(stubControllerComponents(),mockedUserForm,controller,mockedUserInfoRepository,mockedAssignmentInfoRepository)
  }

  case class TestObj(controllerComponent: ControllerComponents,mockedUserForm:UserForm,adminController: AdminController
                     ,mockedUserInfoRepository:UserInfoRepo,mockedAssignmentInfoRepository:AssignmentInfoRepo)

}










