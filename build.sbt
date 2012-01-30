
organization := "Lift"

name := "Basic SquerylRecord User Setup"

version := "0.2-SNAPSHOT"

scalaVersion := "2.9.1"

seq(webSettings :_*)

// If using JRebel
scanDirectories in Compile := Nil

logLevel := Level.Info

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

libraryDependencies ++= {
  val liftVersion = "2.4" // Put the current/latest lift version here
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default" withSources(),
    "net.liftweb" %% "lift-squeryl-record" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-wizard" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-testkit" % liftVersion % "compile->default"
    )
}

// Customize any further dependencies as desired
libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-webapp" % "8.0.3.v20111011" % "test, container",
  //"org.mortbay.jetty" % "jetty" % "6.1.22" % "test, container", // For Jetty 7
  "javax.servlet" % "servlet-api" % "2.5" % "provided->default",
  "org.mindrot" % "jbcrypt" % "0.3m" % "compile->default",
  "com.h2database" % "h2" % "1.2.138", // In-process database, useful for development systems
  "mysql" % "mysql-connector-java" % "5.1.17", //Used in dev-test-jetty-run i.e for development systems outside of jni context
  "postgresql" % "postgresql" % "9.0-801.jdbc4", //Used in dev-test-jetty-run i.e for development systems outside of jni context
  "com.jolbox" % "bonecp" % "0.7.1.RELEASE" % "compile->default",
  "org.slf4j" % "slf4j-log4j12" % "1.6.1" % "compile->default", // Logging
  "junit" % "junit" % "4.8" % "test->default", // For JUnit 4 testing
  "org.scala-tools.testing" %% "specs" % "1.6.9" % "test",
  "org.specs2" %% "specs2" % "1.6.1" % "test"
)
