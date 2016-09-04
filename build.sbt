name := "chess"

version := "1.0"

scalaVersion in Global := "2.11.8"

enablePlugins(ScalaJSPlugin)

lazy val http4sVersion = "0.15.0-SNAPSHOT"

cleanFiles <+= baseDirectory {_ / "static" / "app.js"}

lazy val root = project.in(file("."))
  .aggregate(sharedJS, sharedJVM, backend, frontend)

lazy val shared = crossProject.in(file("shared"))
  .settings(
    name := "shared",
    version := "0.1-SNAPSHOT",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )

lazy val sharedJS = shared.js
lazy val sharedJVM = shared.jvm

lazy val frontend =
  project.in(file("frontend"))
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(sharedJS)
    .settings(
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0"
    )

lazy val backend =
  project.in(file("backend"))
    .settings(
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        "org.http4s" %% "http4s-dsl" % http4sVersion,
        "org.http4s" %% "http4s-blaze-server" % http4sVersion,
        "org.http4s" %% "http4s-blaze-client" % http4sVersion,
        "org.slf4j" % "slf4j-simple" % "1.6.4"
      )
    )

lazy val stage = taskKey[Unit]("stage")

stage := {
  val fullOpt = (fullOptJS in (frontend, Compile)).value
  IO.copyFile(fullOpt.data, file("static/app.js"))
  (assembly in backend).value
}

lazy val compileDev = taskKey[Unit]("compileDev")

compileDev in frontend := {
  val fastOpt = (fastOptJS in (frontend, Compile)).value
  IO.copyFile(fastOpt.data, baseDirectory.value / "static" / "app.js")
  IO.copyFile(fastOpt.data.getParentFile / "frontend-fastopt.js.map", baseDirectory.value / "static" / "frontend-fastopt.js.map")
}

artifactPath in fastOptJS := baseDirectory.value / "static" / "app.js"
