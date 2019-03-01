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
  .settings(
    name := Settings.name + "-frontend"
  )
  .settings(commonSettings: _*)
  .settings(
    // Build a js dependencies file
    //skip in packageJSDependencies := false,
    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),

    // Put the jsdeps file on a place reachable for the server
    //crossTarget in (Compile, packageJSDependencies) := (resourceManaged in Compile).value,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    //dependencyOverrides += "org.webjars.npm" % "js-tokens" % "3.0.2",  // fix for https://github.com/webjars/webjars/issues/1789
    //jsDependencies ++= Settings.jsDependencies.value,
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

    npmDependencies in Compile ++= Seq(
      //"react-event-listener" -> "0.6.6",
      //"@material-ui/core" ->  "3.9.2",

      //"@babel/runtime" ->  "7.3.4",

      //"classnames" ->  "2.2.5",
      //"cross-env" ->  "5.1.2",

      //"i18next" ->  "11.3.2",
      //"i18next-browser-languagedetector" ->  "2.2.0",

      //"material-ui" ->  "1.0.0-beta.4",
      //"material-ui-chip-input" ->  "1.0.0-beta.4",
      //"material-ui-icons" ->  "1.0.0-beta.17",
      //"material-ui-pickers" ->  "1.0.0-rc.9",
      //"moment" ->  "2.22.1",

      "webpack-merge" -> "4.1.0",

      "react-bootstrap" -> "1.0.0-beta.5",
      "bootstrap" -> "4.1.1",
      "jquery" -> "3.2.1",
      "popper.js" -> "1.14.6",

      //"npm" ->  "6.0.0",

      "log4javascript" ->  "1.4.15",

      //"react" ->  "16.3.0",
      "react" ->  "16.8.3",
      "react-dom" ->  "16.8.3"
      //"react-clamp-lines" ->  "1.1.0",
      //"react-custom-scrollbars" ->  "4.2.1",
      //"react-i18next" ->  "7.6.1",
      //"react-popper" ->  "0.10.4",
      //"react-router-dom" ->  "4.2.2",
      //"react-scroll" ->  "1.7.9",
      //"react-select" ->  "1.2.1",
      //"validator" ->  "9.4.1",
      //"@types/recompose" ->  "0.26.1",
      //"recompose" ->  "0.27.1",

      //"i18next-xhr-backend" -> "1.4.3",
      //"uglifyjs-webpack-plugin" -> "^2.0.1",
      //"numeral" -> "~2.0.6"
    ),

    npmDevDependencies in Compile ++= Seq(
      // Bootstrap's CSS
      "css-loader" -> "0.28.9",
      "postcss-loader" -> "^2.1.1",
      "precss" -> "^3.1.2",
      "extract-text-webpack-plugin" -> "3.0.2",
      "file-loader" -> "1.1.6",
      "node-sass" -> "4.9.2",
      "sass-loader" -> "6.0.7",
      "style-loader" -> "0.20.0",
      "url-loader" -> "0.6.2"
    ),

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
    artifactPath in(Compile, packageJSDependencies) := ((crossTarget in(Compile, fastOptJS)).value / "deps.js"),
    artifactPath in(Test, fastOptJS) := ((crossTarget in(Test, fastOptJS)).value / "app.test.js"),
    artifactPath in(Test, packageJSDependencies) := ((crossTarget in(Test, fastOptJS)).value / "deps.test.js"),
    artifactPath in(Compile, fullOptJS) := ((crossTarget in(Compile, fullOptJS)).value / "app.min.js"),
    artifactPath in(Compile, packageMinifiedJSDependencies) := ((crossTarget in(Compile, fullOptJS)).value / "deps.min.js"),
    artifactPath in(Test, fullOptJS) := ((crossTarget in(Test, fullOptJS)).value / "app.min.test.js"),
    artifactPath in(Test, packageMinifiedJSDependencies) := ((crossTarget in(Test, fullOptJS)).value / "deps.min.test.js")
  )
  .enablePlugins(ScalaJSBundlerPlugin,ScalaJSWeb)
  .dependsOn(sharedJs)

lazy val foo = fastOptJS / webpack

lazy val shared =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("shared"))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Settings.sharedDependencies.value
    )
    .enablePlugins(ScalaJSBundlerPlugin,ScalaJSWeb)
    // set up settings specific to the JS project
    //.jsConfigure(_ enablePlugins ScalaJSWeb)


lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js
