
organization := "Lift"

name := "Basic SquerylRecord User Setup"

version := "0.3-SNAPSHOT"

scalaVersion := "2.9.2"

seq(webSettings :_*)

// If using JRebel
scanDirectories in Compile := Nil

logLevel := Level.Info

EclipseKeys.withSource := true

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

libraryDependencies ++= {
  val liftVersion = "2.5.2" // Put the current/latest lift version here
  Seq(
    "net.liftweb" %% "lift-webkit"         % liftVersion % "compile->default" withSources(),
    "net.liftweb" %% "lift-squeryl-record" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper"         % liftVersion % "compile->default",
    "net.liftweb" %% "lift-wizard"         % liftVersion % "compile->default",
    "net.liftweb" %% "lift-testkit"        % liftVersion % "compile->default"
    )
}



// Customize any further dependencies as desired
libraryDependencies <++= scalaVersion { sv =>
  "ch.qos.logback"        % "logback-classic"      % "1.0.0"           % "provided" ::
  "log4j"                 % "log4j"                % "1.2.16"          % "provided" ::
  "org.eclipse.jetty"     % "jetty-webapp"         % "8.0.3.v20111011" % "container" ::
  "org.eclipse.jetty"     % "jetty-plus"           % "8.0.3.v20111011" % "container" :: 
  "org.mindrot"           % "jbcrypt"              % "0.3m"            % "compile->default"::
  "com.jolbox"            % "bonecp"               % "0.7.1.RELEASE"   % "compile->default" ::
  "mysql"                 % "mysql-connector-java" % "5.1.17" ::           
  "postgresql"            % "postgresql"           % "9.0-801.jdbc4" :: 
  "com.h2database"        % "h2"            % "1.3.167" ::  
  (sv match {
      case "2.10.0" | "2.9.2" | "2.9.1" | "2.9.1-1" => "org.specs2" %% "specs2" % "1.12.3" % "test"
      case _ => "org.specs2" %% "specs2" % "1.12.3" % "test"
      }) ::
   (sv match {
      case "2.10.0" | "2.9.2" => "org.scalacheck" %% "scalacheck" % "1.10.0" % "test"
      case _ => "org.scalacheck" %% "scalacheck" % "1.10.0" % "test"
      }) ::
  Nil
}
