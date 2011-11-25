package code.model

import org.squeryl.annotations.Column
import net.liftweb.record.field._
import net.liftweb.record._
import net.liftweb.squerylrecord.KeyedRecord
import net.liftweb.squerylrecord.RecordTypeMode._
import net.liftweb.http.{S, SessionVar, RequestVar, CleanRequestVarOnSessionTransition, RedirectResponse, RedirectWithState, RedirectState}
import net.liftweb.common.{Box, Full, Empty}
import net.liftweb.util.Helpers._
import net.liftweb.sitemap.Loc._
import org.mindrot.jbcrypt.BCrypt


class User private() extends Record[User] with KeyedRecord[Long]
{
	def meta = User 

	@Column(name="id")
	override val idField = new LongField(this)	
	
	val email = new EmailField(this,50)
	val userName = new StringField(this,50)
	val password = new PasswordField(this) with MyPasswordTypedField[User]
	val firstName = new StringField(this,50)
	val lastName = new StringField(this,50)
	val superUser = new BooleanField(this)
	val active = new IntField(this)
	val created = new DateTimeField(this)
	val updated = new DateTimeField(this)
	
	val sex = new BooleanField(this, false) //male - 0, female - 1
	val address = new StringField(this, 255)
	val zip = new StringField(this, 10)
	val city = new StringField(this, 100)
	val tel = new StringField(this, 60)
	val birthday = new OptionalDateTimeField(this)
	
	def fullName: String = lastName + " " + firstName
	
	private def authorizationHashInput = email.is + userName.is + created.is + updated.is
	def makeAuthorizationHash: String = makeAuthorizationHash(randomString(16))
	def makeAuthorizationHash(salt: String) = hash("{"+authorizationHashInput+"} salt={"+salt+"}")+salt
	def validateAuthorizationHash(hash: String): Boolean =
	{
	  if(hash.isEmpty || hash.length <= 28)
	  	false
	  else
	  {
	    val salt = hash.substring(28)
	    hash == makeAuthorizationHash(salt)
	  }
	}
	
}

object User extends User with MetaRecord[User] 
{
	private object curUserId extends SessionVar[Box[Long]](Empty)
	
	def currentUserId: Box[Long] = curUserId.is
	
	private object curUser extends RequestVar[Box[User]](currentUserId.flatMap(get)) with CleanRequestVarOnSessionTransition

	def currentUser: Box[User] = curUser.is	
	
	def logInUser(user: User, doAfterLogin: () => Nothing): Nothing =
	{
		logInUser(user)
		doAfterLogin()
	}
	
	def logInUser(user: User) =
	{
		curUserId.remove
		curUser.remove
		curUserId(Full(user.id))
	}
	
	def logInUserId(id: Long)
	{
		curUser.remove
		curUserId.remove
		curUserId(Full(id))
	}
	
	/**
	 * Checks username and password, if all is ok, logs user in and returns true, else returns false
	 */
	def logIn(username: String, password: String): Boolean =
	{
		getByUserName(username).map(user => 
		{
			if(user.password.match_?(password) && user.active.is > 0)
			{
				logInUser(user)
				true
			}
			else
				false
		}) getOrElse false
	}
	
	def logOut() =
	{
		curUserId.remove
		curUser.remove
		S.session.foreach(_.destroySession)
	}
	
	def logOutAndRedirectToSelf()
	{
	  logOut()
	  S.redirectTo(S.uriAndQueryString openOr "/")
	}
	
	def isLoggedIn : Boolean =
	{
		currentUserId.isDefined
	}
	
	def notLoggedIn : Boolean = !isLoggedIn
	
	def isSuperUser : Boolean = currentUser.map(_.superUser.is) openOr false
	
	var adminLoginPageURL = "/admin/login"
		
	var loginPageURL = "/login"
		
	object loginRedirect extends SessionVar[Box[String]](Empty)
	
	def requireLogin = TestAccess(() => 
	{ 
		if(isLoggedIn) 
			Empty 
		else 
		{
			val uri = S.uriAndQueryString
			Full(RedirectWithState(loginPageURL, RedirectState( ()=>{loginRedirect.set(uri)})))
		}
	})

	def requireAdminLogin = TestAccess(() => 
	{ 
		if(isLoggedIn && isSuperUser) 
			Empty 
		else 
		{
		  if(isLoggedIn)
		    S.error("You don't have administration rights!")
			val uri = S.uriAndQueryString
			Full(RedirectWithState(adminLoginPageURL, RedirectState( ()=>{loginRedirect.set(uri)})))
		}
	})
	
	
	def get(userId: Long): Option[User] = 
		MySchema.users.lookup(userId)

	def getByUserName(username: String): Option[User] = 
	{
		MySchema.users.where(u => lower(u.userName) === username.toLowerCase).headOption			
	}
	
	def getAllUsers: List[User] =
	  from(MySchema.users)(u => select(u)).toList
	
	/**
	 * Checks if a user name exists.
	 * If username.length < 2, returns true (invalid username) 
	 */
	def userNameExists(username: String): Boolean = 
	{
		if (username.length < 2)
			true
		
		from(MySchema.users)(u => where(lower(u.userName) === username.toLowerCase) compute(count)).toLong > 0
	}
	
	/**
	 * Checks if an email address exists.
	 * If email.length < 2, returns true (invalid email) 
	 */
	def emailExists(email: String): Boolean = 
	{
		if (email.length < 2)
			true
		
		from(MySchema.users)(u => where(lower(u.email) === email.toLowerCase) compute(count)).toLong > 0
	}
	
	def currentUserFullName: String =
	{
		User.currentUser match
		{
			case Full(user) => user.fullName
			case _ => ""
		}
	}
	
	def	getNumberOfUsers : Long =
		from(MySchema.users)(u => compute(count)).toLong
}

object PasswordField
{
	@volatile var logRounds = 10
	
	def hashpw(in: String): Box[String] =  tryo(BCrypt.hashpw(in, BCrypt.gensalt(logRounds)))  
}
trait MyPasswordTypedField[OwnerType <: Record[OwnerType]] extends Field[String, OwnerType] with PasswordTypedField
{
	
	def mySalt = 
	{
		val myValue = valueBox.map(v => v.toString) openOr ""
		if(myValue.isEmpty || myValue.length <= 28)
			salt.get
		else
			myValue.substring(28)
	}	

	/*
 	* jBCrypt throws "String index out of range" exception
  	* if password is an empty String
  	*/	
	override def match_?(toTest: String): Boolean = 
	  valueBox.filter(_.length > 0)
	    .flatMap(p => tryo(BCrypt.checkpw(toTest, p)))
	    .openOr(false) 
	  

  override def set_!(in: Box[String]): Box[String] = {
    // to have private validatedValue set
		super.set_!(in)
    in
  }
  
  override def apply(in: Box[MyType]): OwnerType = 
  {
		val hashed = in.map(s => PasswordField.hashpw(s) openOr s)
		super.apply(hashed)
  }
}
