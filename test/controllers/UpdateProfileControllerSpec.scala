package controllers

import akka.util.Timeout
import models.{UserForm, UserInfoRepo}
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.{OK, status, stubControllerComponents}

import scala.concurrent.duration._
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest




class UpdateProfileControllerSpec  extends PlaySpec with Mockito{

  implicit val timeout = Timeout(20 seconds)

  "display Update Profile Form" in {
    val controller = getMockedObject
    when(controller.mockedUserForm.updateProfile) thenReturn {val form = new UserForm{}
      form.updateProfile}
    val result = controller.updateProfileController.profile().apply(FakeRequest().withCSRFToken)
    status(result) must equal(OK)
  }


  def getMockedObject: TestObj = {

    val mockedUserInfoRepository = mock[UserInfoRepo]

    val mockedUserForm = mock[UserForm]

    val controller = new UpdatedProfileController(stubControllerComponents(),mockedUserForm,mockedUserInfoRepository)

    TestObj(stubControllerComponents(),mockedUserForm,controller,mockedUserInfoRepository)
  }

  case class TestObj(controllerComponent: ControllerComponents,mockedUserForm:UserForm, updateProfileController: UpdatedProfileController
                     ,mockedUserInfoRepository:UserInfoRepo)

}




