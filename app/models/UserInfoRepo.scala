package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import slick.lifted.ProvenShape.proveShapeOf
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


//class userInfoRepo @Inject()(cache: AsyncCacheApi){
case class UpdateProfile(fname: String,mname: String,lname:String,age:Int)
case class RegisterdUserInfo(id:Int,fname:String,mname: String,lname:String,email:String,
                             password:String,mobile:String,gender:String,age:Int,
                             isEnable:Boolean = true,
                             isUser:Boolean = true)
class UserInfoRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends Userinforepoimplementation
  with Userinfofirst

trait Userinfofirst extends HasDatabaseConfigProvider[JdbcProfile]
{
  import profile.api._

  private[models] class Userform(tag: Tag) extends Table[RegisterdUserInfo](tag, "RegisteredInfo") {
    def id: Rep[Int] = column[Int]("id",O.AutoInc,O.PrimaryKey)

    def fname: Rep[String] = column[String]("fname")

    def mname: Rep[String] = column[String]("mname")

    def lname: Rep[String] = column[String]("lname")

    def email: Rep[String] = column[String]("email", O.Unique)

    def password: Rep[String] = column[String]("password")

    def mobile: Rep[String] = column[String]("mobile")

    def gender: Rep[String] = column[String]("gender")

    def age: Rep[Int] = column[Int]("age")

    def isEnable: Rep[Boolean] = column[Boolean]("isEnable")

    def isUser: Rep[Boolean] = column[Boolean]("isUser")


    //scalastyle:off
    def * : ProvenShape[RegisterdUserInfo] = (id,fname,mname,lname,email,password,mobile,gender,age,isEnable,isUser) <>(RegisterdUserInfo.tupled, RegisterdUserInfo.unapply)
  }
  //scalastyle:on
  val userQuery: TableQuery[Userform] = TableQuery[Userform]
}
trait Userinforepointerface {
  def store(userinformation: RegisterdUserInfo): Future[Boolean]

  //def findByEmailAndPassword(email: String,password:String): Future[Option[RegisterdUserInfo]]

}
trait Userinforepoimplementation extends Userinforepointerface{
  self: Userinfofirst=>
  import profile.api._

  def store(userinfo: RegisterdUserInfo): Future[Boolean] =
    db.run(userQuery += userinfo) map (_ > 0)

  def isUserRegistered(email: String): Future[Boolean] = {
    val queryResult = userQuery.filter(_.email.toLowerCase === email.toLowerCase).result.headOption
    db.run(queryResult).map{
      case Some(_) =>true
      case None => false
    }
  }

  def validationOfUser(emailId: String,password: String): Future[Boolean] = {
    val queryResult = userQuery.filter(user => user.email === emailId && user.password === password).result.headOption
      db.run(queryResult)
     .map{
        case Some(_) => true
        case None => false
      }
  }

  def isUserCheck(emailId: String): Future[Boolean] = {
    db.run(userQuery.filter(user=> user.email === emailId && user.isUser).to[List].result).map(_.nonEmpty)
  }

  def getUser(): Future[List[RegisterdUserInfo]] = {
    db.run(userQuery.filter(_.isUser===true).to[List].result)
  }

  def enable(emailId: String): Future[Boolean] =
    db.run(userQuery.filter(_.email===emailId).map(_.isEnable).update(true)).map(_>0)

  def disable(emailId: String): Future[Boolean] =
    db.run(userQuery.filter(_.email === emailId).map(_.isEnable).update(false)).map(_>0)

  def isEnableCheck(emailId:String): Future[Boolean] = {
    db.run(userQuery.filter(user => user.email === emailId && user.isEnable).to[List].result).map(_.nonEmpty)
  }


    def updatePassword(emailId:String,newPassword:String): Future[Boolean] =
      db.run(userQuery.filter(_.email===emailId).map(_.password).update(newPassword)).map(_>0)

 def updateUserDetails(email: String, updated: UpdateProfile): Future[Boolean] = {
    db.run(userQuery.filter(_.email === email).map(user => (user.fname, user.mname, user.lname,
      user.age)).update(updated.fname, updated.mname,
      updated.lname, updated.age)).map(_ > 0)
  }

  def getUserDetails(email: String): Future[RegisterdUserInfo] = {
    db.run(userQuery.filter(_.email === email)
      .to[List].result.head)
  }


}


