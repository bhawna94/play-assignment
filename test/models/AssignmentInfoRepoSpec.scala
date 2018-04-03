package models

import org.specs2.mutable.Specification

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

class AssignmentInfoRepoSpec extends Specification {

  val repo = new ModelsTest[AssignmentInfoRepo]


  "Assignment Repository" should {

    "store assignment detaills" in {
      val assignment = AssignmentInfo(1, "lagom", "CRUD Operation")
      val storeResult = Await.result(repo.repository.storeAssignment(assignment), Duration.Inf)
      storeResult must equalTo(true)
    }


    "reterive list of assignment" in {
      val assignment = List(AssignmentInfo(1, "lagom", "CRUD Operation"))
      val storeResult = Await.result(repo.repository.getAssignment(), Duration.Inf)
      storeResult must equalTo(assignment)
    }






    /* "delete list of assignment" in {
      val storeResult = Await.result(repo.repository.deleteAssignment(1),Duration.Inf)
      storeResult must equalTo(true)
    }
  }*/
  }
}



