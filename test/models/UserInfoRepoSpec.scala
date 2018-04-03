package models

import org.specs2.mutable.Specification

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

class UserInfoRepoSpec extends Specification {

  val repo = new ModelsTest[UserInfoRepo]


  "User Repository" should {

    "store user details" in {
      val user = RegisterdUserInfo(1, "bhawna", "", "Sharma", "94sharma@gmail",
        "123456abcdef", "9818251174", "Female", 20,
        true, false)
      val storeResult = Await.result(repo.repository.store(user), Duration.Inf)
      storeResult must equalTo(true)
    }


    "validation of user" in {
      val email = "94sharma@gmail"
      val password = "123456abcdef"
      val storeResult = Await.result(repo.repository.validationOfUser(email, password), Duration.Inf)
      storeResult must equalTo(true)
    }


    "Unable to validate user" in {
      val email = "94sharma@gmail"
      val password = "123456abcd"
      val storeResult = Await.result(repo.repository.validationOfUser(email, password), Duration.Inf)
      storeResult must equalTo(false)
    }


    "Whether user registered" in {
      val email = "94sharma@gmail"
      val storeResult = Await.result(repo.repository.isUserRegistered(email), Duration.Inf)
      storeResult must equalTo(true)
    }

    "When user is not registered" in {
      val email = "94sharma@gma"
      val storeResult = Await.result(repo.repository.isUserRegistered(email), Duration.Inf)
      storeResult must equalTo(false)
    }


    "Whether a person is a user" in {
      val email = "94sharma@gmail"
      val storeResult = Await.result(repo.repository.isUserCheck(email), Duration.Inf)
      storeResult must equalTo(false)
    }

    "list of user" in {
      val user = List(RegisterdUserInfo(1, "bhawna", "s", "Sharma", "94sharma@gmail",
        "123456abcdef", "9818251174", "Female", 20,
        true, false))
      val email = "94sharma@gmail"
      val storeResult = Await.result(repo.repository.getUser(), Duration.Inf)
      storeResult must equalTo(List())
    }

    "Update user as enable" in {
      val email = "94sharma@gmail"
      val storeResult = Await.result(repo.repository.enable(email), Duration.Inf)
      storeResult must equalTo(true)
    }
    "Update user as disable" in {
      val email = "94sharma@gmail"
      val storeResult = Await.result(repo.repository.disable(email), Duration.Inf)
      storeResult must equalTo(true)
    }

    "Whether user is enable" in {
      val email = "94sharma@gmail"
      val storeResult = Await.result(repo.repository.isEnableCheck(email), Duration.Inf)
      storeResult must equalTo(false)
    }

    "Update password" in {
      val email = "94sharma@gmail"
      val password = "123456abcdef"
      val storeResult = Await.result(repo.repository.updatePassword(email, password), Duration.Inf)
      storeResult must equalTo(true)
    }

    "Update User Details" in {
      val email = "94sharma@gmail"
      val updatedValue = UpdateProfile("bhawna", "s", "Sharma", 20)
      val storeResult = Await.result(repo.repository.updateUserDetails(email, updatedValue), Duration.Inf)
      storeResult must equalTo(true)
    }


    "Get User List" in {
      val email = "94sharma@gmail"
      val user = RegisterdUserInfo(1, "bhawna", "s", "Sharma", "94sharma@gmail",
        "123456abcdef", "9818251174", "Female", 20,
        false, false)
      val storeResult = Await.result(repo.repository.getUserDetails(email), Duration.Inf)
      storeResult must equalTo(user)
    }

  }
}

