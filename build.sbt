import sbt.Def
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}
//import webscalajs.ScalaJSWeb

// Top-level settings

enablePlugins(ScalafmtPlugin, JavaAppPackaging, GhpagesPlugin, MicrositesPlugin, TutPlugin)

git.remoteRepo := "git@github.com:pauljamescleary/scala-pet-store.git"

micrositeGithubOwner := "pauljamescleary"

micrositeGithubRepo := Settings.name

micrositeName := "Scala Pet Store"

micrositeDescription := "An example application using FP techniques in Scala"

micrositeBaseUrl := Settings.name

// Settings for backend, frontend, and shared code

// Filter out compiler flags to make the repl experience functional...
val badConsoleFlags = Seq("-Xfatal-warnings", "-Ywarn-unused:imports")

lazy val commonSettings = Def.settings(
  scalaVersion := "2.12.8",
  organization := "io.github.pauljamescleary",
  version      := Settings.version,
  scalacOptions ++= Settings.scalacOptions,
  scalacOptions in (Compile, console) ~= (_.filterNot(badConsoleFlags.contains(_))),
  resolvers += Resolver.sonatypeRepo("snapshots")
)

// This function allows triggered compilation to run only when scala files changes
// It lets change static files freely
def includeInTrigger(f: java.io.File): Boolean =
  f.isFile && {
    val name = f.getName.toLowerCase
    name.endsWith(".scala") || name.endsWith(".js")
  }

lazy val backend = (project in file("backend"))
  .settings(
    name := Settings.name
  )
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Settings.backendDependencies.value,
    // Allows to read the generated JS on client
    resources in Compile += (fastOptJS in (frontend, Compile)).value.data,
    // Lets the backend to read the .map file for js
    resources in Compile += (fastOptJS in (frontend, Compile)).value
      .map((x: sbt.File) => new File(x.getAbsolutePath + ".map"))
      .data,
    // Lets the server read the jsdeps file
    (managedResources in Compile) += (artifactPath in (frontend, Compile, packageJSDependencies)).value,
    // do a fastOptJS on reStart
    reStart := (reStart dependsOn (fastOptJS in (frontend, Compile))).evaluated,
    // This settings makes reStart to rebuild if a scala.js file changes on the client
    watchSources ++= (watchSources in frontend).value,
    // Support stopping the running server
    mainClass in reStart := Some("io.github.pauljamescleary.petstore.Server"),
    fork in run := true,
  )
  .dependsOn(sharedJvm)

///

lazy val frontend = (project in file("frontend"))
  .settings(
    name := Settings.name + "-frontend"
  )
  .settings(commonSettings: _*)
  .settings(
    // Build a js dependencies file
    skip in packageJSDependencies := false,
    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),

    // Put the jsdeps file on a place reachable for the server
    crossTarget in (Compile, packageJSDependencies) := (resourceManaged in Compile).value,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    libraryDependencies ++= Settings.scalajsDependencies.value,
    dependencyOverrides += "org.webjars.npm" % "js-tokens" % "3.0.2",  // fix for https://github.com/webjars/webjars/issues/1789
    jsDependencies ++= Settings.jsDependencies.value,
    // use Scala.js provided launcher code to start the client app
    mainClass in Compile := Some("io.github.pauljamescleary.petstore.frontend.PetstoreApp"),
    scalaJSUseMainModuleInitializer := true,
    scalaJSUseMainModuleInitializer in Test := false
  )
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJs)


lazy val shared =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("shared"))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Settings.sharedDependencies.value
    )
    // set up settings specific to the JS project
    //.jsConfigure(_ enablePlugins ScalaJSWeb)


lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js
