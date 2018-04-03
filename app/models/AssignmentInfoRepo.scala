package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AssignmentInfo(id:Int,title: String,description: String)
class AssignmentInfoRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AssignmentInfoRepoImplementation
  with AssignmentInfoFirst

trait AssignmentInfoFirst extends HasDatabaseConfigProvider[JdbcProfile]
{
  import profile.api._

  private[models] class AssignForm(tag: Tag) extends Table[AssignmentInfo](tag, "AssignmentDetails") {

    def id: Rep[Int] = column[Int]("id",O.AutoInc,O.PrimaryKey)

    def title: Rep[String] = column[String]("title")

    def description: Rep[String] = column[String]("description")

    //scalastyle:off
    def * : ProvenShape[AssignmentInfo] = (id,title,description) <>(AssignmentInfo.tupled, AssignmentInfo.unapply)
  }
  //scalastyle:on
  val assignmentQuery: TableQuery[AssignForm] = TableQuery[AssignForm]
}
trait AssignmentInfoRepoInterface {
  def storeAssignment(assignInformation: AssignmentInfo): Future[Boolean]

  def getAssignment(): Future[List[AssignmentInfo]]

  def deleteAssignment(id: Int): Future[Boolean]

  //def findByEmailAndPassword(email: String,password:String): Future[Option[RegisterdUserInfo]]

}
trait AssignmentInfoRepoImplementation extends AssignmentInfoRepoInterface{
  self: AssignmentInfoFirst=>
  import profile.api._

  def storeAssignment(assignInformation: AssignmentInfo): Future[Boolean] =
    db.run(assignmentQuery += assignInformation).map (_ > 0)

  def getAssignment(): Future[List[AssignmentInfo]]=
    db.run(assignmentQuery.to[List].result)

  def deleteAssignment(id: Int): Future[Boolean] = {
    db.run(assignmentQuery.filter(_.id === id).delete.map(_> 0))
  }
}
