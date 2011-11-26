package code.snippet

import scala.xml.{ NodeSeq, Text }
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import java.util.Date
import code.lib._
import code.model._
import Helpers._
import net.liftweb.sitemap._
import scala.xml.Text

/*
 * This is a fast-track and minimal login or "open registration" with "auto" login snippet. 
 * If you need more initial values or validation, just extend it or keep login/reg. simple 
 * and postpone a more extensive registration to later on a user "My pages" page.  
 * If provided parameter values passes validation one of two things happens:
 * 1) Try to login a known user. A known user is a user who provides a known email 
 *  If this user provides a corresponding correct password the user will be logged in 
 *  else login will fail. 
 * Yes this registration uses email "as username" but you can easily tweak it to your liking.  
 * 2) Try to register a unknown/new user and login the user. A unknown 
 * user is a user whose email is unknown. Passing validation of password and email the
 * registration will succeed and the user will be logged in.
 */
object RegisterLogin extends Loggable {
  val menu = Menu("Register", User.isLoggedIn match { case true => "Logout" case _ => "Register/Login" }) / "registerLogin"
}
class RegisterLogin extends StatefulSnippet with Loggable {

  private object email extends RequestVar("")
  private object conf_email extends RequestVar("")
  private object pw extends RequestVar("")
  private object conf_pw extends RequestVar("")
  private object referer extends RequestVar(S.referer openOr "/registerLogin")

  def dispatch = {
    case "registerOrLogin" => registerOrLogin
    case "logout"          => logout
    case "greetings"       => greetings
  }

  def registerOrLogin = {
    def processRegisterOrLogin() = {
      logger.debug("processRegister: email="+email+" confirm email="+conf_email+" pw="+pw+" referer="+referer+" confirm pw="+conf_pw)
      Validator.isValidEmail(email) match { //require valid email as we are using email as registration userName 
        case true => {
          Validator.isValidPw(pw) match { //set some constraints on the pw
            case true => {
              Validator.isNewUser(email) match { //all is well check if new or known user
                case true => { //yes it is a new user 
                  Validator.isValidPWConfirmation(pw, conf_pw) match {
                    case true => { //oki doki prob. no typing errors in email address.
                      //minimal registration of new user, don't forget to set active>0 or user wont be able to login
                      val r = User.createRecord.userName(email).email(email).password(pw).active(1)
                      MySchema.users.insert(r)
                      //login the new user
                      Validator.isValidDoLogin(email, pw) match {
                        case true => {
                          logger.debug("New user loggin ok currentUserId"+User.currentUserId)
                        }
                        case false => {
                          logger.debug("New user Invalid login data for user "+email)
                          S.error("pw", "New user Invalid login data!")
                        }
                      }
                    }
                    case false => {
                      logger.debug("New user registration passwords dose not match")
                      S.error("pw", "Passwords dose not match")
                    }
                  }
                }
                case false => { // no this is not a new user 
                  //try login known user
                  Validator.isValidDoLogin(email, pw) match {
                    case true => {
                      logger.debug("user loggin ok currentUserId"+User.currentUserId)
                    }
                    case false => { S.error("pw", "Wrong credentials!") }
                  }
                }
              }
            }
            case false => { S.error("pw", "Invalid credentials!") }
          }
        }
        case false => { S.error("email", "Invalid emailaddres!") }
      }
    }
    val r = referer.is
    //logger.debug("referer.is="+referer.is)
    "name=email" #> SHtml.email(email) &
      /*"name=conf_email" #> SHtml.textElem(conf_email) &*/
      "name=conf_pw" #> SHtml.password("", (x) => conf_pw.set(x), "id" -> "input_conf_pw") &
      "name=pw" #> (SHtml.password("", (x) => pw.set(x), "id" -> "input_pw" , "title" -> "Minimum 6 alphanumeric characters", "pattern" -> "\\w{6,}", "placeholder" -> "password") ++ SHtml.hidden(() => referer.set(r))) &
      "type=submit" #> SHtml.onSubmitUnit(processRegisterOrLogin)
  }

  def greetings = {
    def greeting() = {
      val ret = if (User.fullName.trim.length() > 0) {
        logger.debug("trying to greet with user fullname")
        Text(User.currentUserFullName)
      } else {
        logger.debug("trying to greet with userName (should be same as email) "+User.currentUser.openTheBox.userName.is)
        Text(User.currentUser.openTheBox.userName.is)
      }
      ret
    }
    "#user_greeter *" #> greeting()
  }

  def logout = {
    def processLogout() = {
      User.logOut()
    }
    "type=submit" #> SHtml.onSubmitUnit(processLogout)
  }

}

object Validator extends Loggable {
  def isNewUser(email: String): Boolean = {
    !(User.userNameExists(email))
  }
  def isValidDoLogin(email: String, pw: String): Boolean = {
    if (pw.trim.length > 4) { User.logIn(email, pw) } else { false }
  }
  def isValidPw(pw: String): Boolean = {
    if (pw.trim.length > 4) { true } else { false }
  }
  def isValidEmail(email: String): Boolean = {
    import net.liftweb.mapper.{ MappedEmail }
    MappedEmail.validEmailAddr_?(email)
  }

  def isValidPWConfirmation(pw: String, conf_pw: String): Boolean = {
    if (pw.trim.equals(conf_pw.trim)) true else false
  }
  /*
  def isValidPassword(pw:String,conf_pw:String):Boolean = {
    if(pw.trim.equals(conf_pw.trim) && pw.trim.length>4) true else false
  }
  */
}

