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




  class RegistrationControllerSpec  extends PlaySpec with Mockito{

    implicit val timeout = Timeout(20 seconds)

    "display Registration Form" in {
      val controller = getMockedObject
      when(controller.mockedUserForm.userForm) thenReturn {val form = new UserForm{}
        form.userForm}
      val result = controller.registrationController.register().apply(FakeRequest().withCSRFToken)
      status(result) must equal(OK)
    }


    def getMockedObject: TestObj = {

    val mockedUserInfoRepository = mock[UserInfoRepo]

    val mockedUserForm = mock[UserForm]

    val controller = new RegistrationController(stubControllerComponents(),mockedUserForm,mockedUserInfoRepository)

    TestObj(stubControllerComponents(),mockedUserForm,controller,mockedUserInfoRepository)
  }

  case class TestObj(controllerComponent: ControllerComponents,mockedUserForm:UserForm,registrationController: RegistrationController
                     ,mockedUserInfoRepository:UserInfoRepo)

}



