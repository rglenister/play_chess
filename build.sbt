name := "Chess"

version := "1.0"

/*scalaVersion := "2.9.1"*/

libraryDependencies ++= Seq(
  "cglib" % "cglib" % "2.2.2",
  "junit" % "junit" % "4.10",
  "org.easymock" % "easymock" % "3.1",
  "org.scalamock" %% "scalamock-scalatest-support" % "latest.integration",
/*  "org.scalatest" %% "scalatest" % "1.7.1" % "test", */
  "org.scalactic" %% "scalactic" % "3.2.15",
  "org.scalatest" %% "scalatest" % "3.2.15" % "test",
  "org.scalatestplus" %% "junit-4-13" % "3.2.15.0" % "test",
  "org.objenesis" % "objenesis" % "1.2",
  "log4j" % "log4j" % "1.2.16"
)