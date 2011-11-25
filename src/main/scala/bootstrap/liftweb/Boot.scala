package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._
import common.{Box,Full,Empty,Loggable}
import http._
import sitemap._
import Loc._
import code.model._
import net.liftweb.squerylrecord.RecordTypeMode._
import code.model.MySchema
import code.snippet.RegisterLogin

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Loggable {
  def boot {
        
    // where to search snippet
    LiftRules.addToPackages("code")
    
    /*un-comment and switch to db of your liking */
    MySchemaHelper.initSquerylRecordWithInMemoryDB
    //MySchemaHelper.initSquerylRecordWithMySqlDB
    //MySchemaHelper.initSquerylRecordWithPostgresDB
    
    Props.mode match {
      case Props.RunModes.Development => { 
        logger.info("RunMode is DEVELOPMENT") 
        /*OBS! do no use this in a production env*/
        MySchemaHelper.dropAndCreateSchema
        }
      case Props.RunModes.Production => logger.info("RunMode is PRODUCTION") 
      case _ => logger.info("RunMode is TEST, PILOT or STAGING")                                       
    }    
    
    def loggedIn = {
      () => { 
        User.isLoggedIn match {
          case true => Empty
          case false => { 
            //redirect and set notice message 
            Full(S.redirectTo("/registerLogin",() => S.notice("","You need to login to access this page!")))
          }
        }
      }
    }

    // Build SiteMap
    def sitemap = SiteMap(
      Menu.i("Home") / "index",  // the simple way to declare a menu
       Menu.i("My Pages") / "user" / "index" >> EarlyResponse(loggedIn), //Show the page but redirect if user is not logged in
      RegisterLogin.menu, //the register/login snippet menu part
     
      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")))

    //def sitemapMutators = User.sitemapMutator

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemap/*sitemapMutators(sitemap)*/)

    // Use jQuery 1.4
    LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQuery14Artifacts

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.isLoggedIn)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))    

    //notice fade out (start after x, fade out duration y)
    LiftRules.noticesAutoFadeOut.default.set( (notices: NoticeType.Value) => {
        notices match {
          case  NoticeType.Notice => { 
            //logger.debug("Notice has been detected and fadeout is set ") 
            Full((8 seconds, 4 seconds)) 
            }
          case _ => { 
            Empty 
            }
        }
     }
    )        
      
    // Make a transaction span the whole HTTP request
    S.addAround(new LoanWrapper
    {
    	override def apply[T](f: => T): T = 
    	{
    		inTransaction{ f }
    	}
    })
    
  }
}
