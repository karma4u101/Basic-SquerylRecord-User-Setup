User Authentication Authorization Using SquerylRecord
=====================================================

This Scala/Lift basic assembly example project demonstrates a basic squeryl-record ORM/DSL User (authentication/authorization) setup 
with register/login/user-pages snippets. To make things easy to follow the examples has extensive logging. 


Quick Start
-----------
The only prerequisites for running this Lift example is that you have Git and Java installed and configured on the target computer.
You don't need to use it but the project also includes a Eclipse plug-in for browsing and following/working with the code, see the Scala IDE section.   


1) Get the examples

	git clone git@github.com:karma4u101/Basic-SquerylRecord-User-Setup.git
	cd Basic-SquerylRecord-User-Setup

2) Update & Run Jetty

There is also a sbt.bat for windows users.

	./sbt update ~container:start

3) Launch Your Browser
	
	http://localhost:8080/
	
Changing Database back end
--------------------------
For demonstration purpose and easy setup this example uses (by default) a in memory db but you can easily change database by 
un-comment the corresponding db init line in Boot.scala. Database name, user and password is set in the default.props resources file.  	

Scala IDE for Eclipse
---------------------
Sbteclipse provides SBT command to create Eclipse project files

1) Usage

	project$ ./sbt
	> eclipse create-src

2) In eclipse do: 

	File ==> Import...
	Select General ==> Existing Project into Workspace 
	Use "Brows" to look up the project root ....

