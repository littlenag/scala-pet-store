import sbt.Def
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

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

lazy val backend = (project in file("backend"))
  .settings(
    name := Settings.name
  )
  .settings(commonSettings: _*)
  .settings(
    scalaJSProjects := Seq(frontend,sharedJs),
    pipelineStages in Assets := Seq(scalaJSPipeline),

    libraryDependencies ++= Settings.backendDependencies.value,

    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,

    // do a fastOptJS on reStart
    reStart := (reStart dependsOn ((fastOptJS / webpack) in (frontend, Compile))).evaluated,

    // This settings makes reStart to rebuild if a scala.js file changes on the client
    watchSources ++= (watchSources in frontend).value,

    // Support stopping the running server
    mainClass in reStart := Some("io.github.pauljamescleary.petstore.Server"),

    fork in run := true,
  )
  .dependsOn(sharedJvm)
  .enablePlugins(SbtWeb, WebScalaJSBundlerPlugin)

///

// https://github.com/scalacenter/scalajs-bundler/issues/111
lazy val npmDevOverrides = Seq( "source-map-loader" -> "git+https://github.com/shishkin/source-map-loader#fetch-http-maps" )

lazy val frontend = (project in file("frontend"))
  //.settings(name := Settings.name + "-frontend")
  .settings(commonSettings:_*)
  .settings(
    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),

    testFrameworks += new TestFramework("utest.runner.Framework"),

    // use Scala.js provided launcher code to start the client app
    mainClass in Compile := Some("io.github.pauljamescleary.petstore.frontend.PetstoreApp"),
    scalaJSUseMainModuleInitializer := true,
    scalaJSUseMainModuleInitializer in Test := false,
    scalaJSLinkerConfig := {
      val fastOptJSURI = (artifactPath in (Compile, fastOptJS)).value.toURI
      scalaJSLinkerConfig.value
        .withRelativizeSourceMapBase(Some(fastOptJSURI))
        .withOptimizer(false)
        .withSourceMap(true)
        .withPrettyPrint(true)
    },
    scalaJSLinkerConfig in (Compile, fullOptJS) ~= { _.withSourceMap(false) },

    libraryDependencies ++= Settings.frontendDependencies.value,

    npmDependencies in Compile ++= Settings.npmDeps,
    npmDevDependencies in Compile ++= Settings.npmDevDeps,

    // Use a custom config file to export the JS dependencies to the global namespace,
    // as expected by the scalajs-react facade
    webpackConfigFile := Some(baseDirectory.value / "webpack.config.js"),

    // https://github.com/scalacenter/scalajs-bundler/issues/111
    version in webpack := "4.28.1",
    version in startWebpackDevServer := "3.1.14",
    npmDevDependencies in Compile ++= npmDevOverrides,
    npmResolutions in Compile := npmDevOverrides.toMap,

    // Uniformises fastOptJS/fullOptJS output file name
    artifactPath in(Compile, fastOptJS) := ((crossTarget in(Compile, fastOptJS)).value / "app.js"),
    artifactPath in(Test, fastOptJS) := ((crossTarget in(Test, fastOptJS)).value / "app.test.js"),
    artifactPath in(Compile, fullOptJS) := ((crossTarget in(Compile, fullOptJS)).value / "app.min.js"),
    artifactPath in(Test, fullOptJS) := ((crossTarget in(Test, fullOptJS)).value / "app.min.test.js")
    //artifactPath in(Compile, packageJSDependencies) := ((crossTarget in(Compile, fastOptJS)).value / "deps.js"),
    //artifactPath in(Test, packageJSDependencies) := ((crossTarget in(Test, fastOptJS)).value / "deps.test.js"),
    //artifactPath in(Compile, packageMinifiedJSDependencies) := ((crossTarget in(Compile, fullOptJS)).value / "deps.min.js"),
    //artifactPath in(Test, packageMinifiedJSDependencies) := ((crossTarget in(Test, fullOptJS)).value / "deps.min.test.js")
  )
  .enablePlugins(ScalaJSBundlerPlugin,ScalaJSWeb)
  .dependsOn(sharedJs)

lazy val shared =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("shared"))
    .settings(commonSettings:_*)
    .settings(
      //name := Settings.name + "-shared",
      libraryDependencies ++= Settings.sharedDependencies.value,

      // Uniformises fastOptJS/fullOptJS output file name
      artifactPath in(Compile, fastOptJS) := ((crossTarget in(Compile, fastOptJS)).value / "shared.js"),
      artifactPath in(Test, fastOptJS) := ((crossTarget in(Test, fastOptJS)).value / "shared.test.js"),
      artifactPath in(Compile, fullOptJS) := ((crossTarget in(Compile, fullOptJS)).value / "shared.min.js"),
      artifactPath in(Test, fullOptJS) := ((crossTarget in(Test, fullOptJS)).value / "shared.min.test.js"),
    )
    .enablePlugins(ScalaJSBundlerPlugin,ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js