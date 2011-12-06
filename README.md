User Authentication Authorization Using SquerylRecord
=====================================================

This Scala/Lift basic assembly example project demonstrates a very basic squeryl-record ORM/DSL User (authentication/authorization) setup 
with register/login/user-pages snippets. The example also demonstrates some form data validation by the snippet before being posted to the 
database as well as a visual interactive html5 form field validation. To make things easy to follow the examples has extensive logging. 

This example is intended for those of you who need some compact and concise running and mostly ;) simple to follow example of a particular 
feature or concept in Lift.

After many years of Java EE development I started look at Scala/Lift in May 2011 so there may be some none "best practice" stuff in there 
and if you find something you think could be done in a more Scala/Lift fashion please let me know.

Improvements and suggestions are welcome!  

best regards 
Peter Petersson     


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
	
Changing Database backend
-------------------------
For demonstration purpose and easy setup this example uses, by default, a in memory database with name "testXYZDB" but you can  
out of the box change database to MySql or Postgres by uncomment the corresponding database init line in Boot.scala. Database name, 
user and password is set in the default.props resources file. With a few lines of code you can enable support for any Squeryl supported db. 	

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

