package controllers


import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents
import play.api.test.Helpers._

class HomeControllerSpec extends PlaySpec with Mockito{


  "go to index page" in {
    val controller = getMockedObject
    val result = controller.homeController.index().apply(FakeRequest())
    status(result) must equal (OK)
  }




  def getMockedObject: TestObj = {

    val controller = new HomeController(stubControllerComponents())

    TestObj(stubControllerComponents(),controller)
  }

  case class TestObj(controllerComponent: ControllerComponents,homeController: HomeController)

}




