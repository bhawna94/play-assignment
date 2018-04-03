package controllers

import models.{AssignmentInfoRepo, UserInfoRepo}


import models.{AssignmentInfo}
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents
import play.api.test.Helpers._


import scala.concurrent.Future

class ProfileControllerSpec extends PlaySpec with Mockito{


  "go to user profile page" in {
    val controller = getMockedObject
    val result = controller.profileController.userProfile().apply(FakeRequest())
    status(result) must equal (OK)
  }


   "view Assignment" in {
    val controller = getMockedObject
    val assignment = List(AssignmentInfo(1,"Play","registration"))

    when(controller.mockedAssignmentInfoRepository.getAssignment()) thenReturn Future.successful(assignment)
    val result = controller.profileController.viewAssignmentByUser().apply(FakeRequest())
    status(result) must equal (OK)
  }


  def getMockedObject: TestObj = {

    val mockedUserInfoRepository = mock[UserInfoRepo]
    val mockedAssignmentInfoRepository = mock[AssignmentInfoRepo]

    val controller = new ProfileController(stubControllerComponents(),mockedAssignmentInfoRepository,mockedUserInfoRepository)

    TestObj(stubControllerComponents(),controller,mockedUserInfoRepository,mockedAssignmentInfoRepository)
  }

  case class TestObj(controllerComponent: ControllerComponents,profileController: ProfileController
                     ,mockedUserInfoRepository:UserInfoRepo,mockedAssignmentInfoRepository:AssignmentInfoRepo)

}




