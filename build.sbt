
// The simplest possible sbt build file is just one line:

scalaVersion := "2.13.3"
// That is, to create a valid sbt build, all you've got to do is define the
// version of Scala you'd like your project to use.

// ============================================================================

// Lines like the above defining `scalaVersion` are called "settings". Settings
// are key/value pairs. In the case of `scalaVersion`, the key is "scalaVersion"
// and the value is "2.13.3"

// It's possible to define many kinds of settings, such as:

name := "hello-world"
organization := "ch.epfl.scala"
version := "1.0"

val slickVersion = "3.2.3"

val AkkaVersion = "2.6.14"

Global / onChangedBuildSource := ReloadOnSourceChanges

// Note, it's not required for you to define these three settings. These are
// mostly only necessary if you intend to publish your library's binaries on a
// place like Sonatype or Bintray.


// Want to use a published library in your project?
// You can define other libraries as dependencies in your build like this:

// libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"

// Here, `libraryDependencies` is a set of dependencies, and by using `+=`,
// we're adding the scala-parser-combinators dependency to the set of dependencies
// that sbt will go and fetch when it starts up.
// Now, in any Scala file, you can import classes, objects, etc., from
// scala-parser-combinators with a regular import.

// TIP: To find the "dependency" that you need to add to the
// `libraryDependencies` set, which in the above example looks like this:

// "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"

// You can use Scaladex, an index of all known published Scala libraries. There,
// after you find the library you want, you can just copy/paste the dependency
// information that you need into your build file. For example, on the
// scala/scala-parser-combinators Scaladex page,
// https://index.scala-lang.org/scala/scala-parser-combinators, you can copy/paste
// the sbt dependency from the sbt box on the right-hand side of the screen.

// IMPORTANT NOTE: while build files look _kind of_ like regular Scala, it's
// important to note that syntax in *.sbt files doesn't always behave like
// regular Scala. For example, notice in this build file that it's not required
// to put our settings into an enclosing object or class. Always remember that
// sbt is a bit different, semantically, than vanilla Scala.

// ============================================================================

// Most moderately interesting Scala projects don't make use of the very simple
// build file style (called "bare style") used in this build.sbt file. Most
// intermediate Scala projects make use of so-called "multi-project" builds. A
// multi-project build makes it possible to have different folders which sbt can
// be configured differently for. That is, you may wish to have different
// dependencies or different testing frameworks defined for different parts of
// your codebase. Multi-project builds make this possible.

// Here's a quick glimpse of what a multi-project build looks like for this
// build, with only one "subproject" defined, called `root`:

//lazy val tutorial = taskKey[Unit]("tutorial")
//lazy val subTask = taskKey[Unit]("sub-task")
//tutorial := {
//  println("Hello, SBT!")
//  Seq(1).map
//}
lazy val helloWorld = (project in file(".")).
 settings(
   inThisBuild(List(
     organization := "ch.epfl.scala",
     scalaVersion := "2.13.3"
   )),
   name := "hello-world"
 )

lazy val rational = (project in file("rational")).
  settings(
    inThisBuild(List(
      organization := "ch.epfl.scala",
      scalaVersion := "2.13.3"
    )),
    name := "rational"
  )

lazy val akkaStream = (project in file("akka-stream")).
  settings(
    inThisBuild(List(
      organization := "ch.epfl.scala",
      scalaVersion := "2.13.3"
    )),
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    name := "akka-stream"
  )

lazy val liquibase = (project in file("liquibase")).
  settings(
    inThisBuild(List(
      organization := "xtp",
      scalaVersion := "2.13.3"
    )),
    libraryDependencies += "org.liquibase" % "liquibase-core" % "4.7.0",
    name := "liquibase"
  )

//lazy val slickPlay = (project in (file("slick-play")))
//  .settings(
//    name := "slick-play",
//    libraryDependencies ++= Seq(
//      "mysql" % "mysql-connector-java" % "8.0.25",
//      "org.slf4j" % "slf4j-nop" % "1.6.4",
//      "com.typesafe.slick" %% "slick" % slickVersion,
//      "com.typesafe.slick" %% "slick-codegen" % slickVersion,
//      "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
//    )
//  )

val circeVersion = "0.14.1"
lazy val circe = (project in (file("circe")))
  .settings(
    name := "circe",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion)
  )


// To learn more about multi-project builds, head over to the official sbt
// documentation at http://www.scala-sbt.org/documentation.html
