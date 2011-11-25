package code
package model

import lib.util._

object DemoData {

  def createDemoData {
    val usersList = prepareUser()
    usersList.foreach(MySchema.users.insert(_))
  }
  
  def prepareUser(): List[User] = {
    List()
  }

}