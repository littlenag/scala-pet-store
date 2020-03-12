import sbt.Def
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

// Top-level settings

//enablePlugins(ScalafmtPlugin, JavaAppPackaging, GhpagesPlugin, MicrositesPlugin, TutPlugin)

//git.remoteRepo := "git@github.com:pauljamescleary/scala-pet-store.git"

//micrositeGithubOwner := "pauljamescleary"

//micrositeGithubRepo := Settings.name

//micrositeName := "Scala Pet Store"

//micrositeDescription := "An example application using FP techniques in Scala"

//micrositeBaseUrl := Settings.name

// Settings for server, client, and shared code

// Filter out compiler flags to make the repl experience functional...
val badConsoleFlags = Seq("-Xfatal-warnings", "-Ywarn-unused:imports")

lazy val commonSettings = Def.settings(
  scalaVersion := "2.12.8",  // 2.13.1?
  organization := "io.github.pauljamescleary",
  version      := Settings.version,
  scalacOptions ++= Settings.scalacOptions,
  scalacOptions in (Compile, console) ~= (_.filterNot(badConsoleFlags.contains(_))),
  //resolvers += Resolver.sonatypeRepo("snapshots"),

  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
)

lazy val server = (project in file("server"))
  .settings(
    name := Settings.name
  )
  .settings(commonSettings: _*)
  .settings(
    scalaJSProjects := Seq(client,sharedJs),
    pipelineStages in Assets := Seq(scalaJSPipeline),

    // https://github.com/sbt/sbt-web#packaging-and-publishing
    //WebKeys.packagePrefix in Assets := "public/",

    libraryDependencies ++= Settings.serverDependencies.value,

    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,

    // do a fastOptJS on reStart
    reStart := (reStart dependsOn ((fastOptJS / webpack) in (client, Compile))).evaluated,

    // This settings makes reStart to rebuild if a scala.js file changes on the client
    watchSources ++= (watchSources in client).value,

    // Support stopping the running server
    mainClass in reStart := Some("io.github.pauljamescleary.petstore.Server"),

    fork in run := true,

    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "io.github.pauljamescleary",
  )
  .dependsOn(sharedJvm)
  .enablePlugins(SbtWeb, WebScalaJSBundlerPlugin)
  .enablePlugins(BuildInfoPlugin)

///

// https://github.com/scalacenter/scalajs-bundler/issues/111
lazy val npmDevOverrides = Seq( "source-map-loader" -> "git+https://github.com/shishkin/source-map-loader#fetch-http-maps" )

// use eliding to drop some debug code in the production build
lazy val elideOptions = settingKey[Seq[String]]("Set limit for elidable functions")

lazy val client = (project in file("client"))
  .settings(commonSettings:_*)
  .settings(
    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),

    // TODO change to scalatest
    testFrameworks += new TestFramework("utest.runner.Framework"),

    elideOptions := Seq("-Xelide-below", "MINIMUM"),

    scalacOptions ++= elideOptions.value,

    // use Scala.js provided launcher code to start the client app
    mainClass in Compile := Some("io.github.pauljamescleary.petstore.client.PetstoreApp"),
    scalaJSUseMainModuleInitializer := true,
    scalaJSUseMainModuleInitializer in Test := false,
    scalaJSLinkerConfig := {
      //val fastOptJSURI = (artifactPath in (Compile, fastOptJS)).value.toURI
      scalaJSLinkerConfig.value
      .withOptimizer(false)
      .withSourceMap(true)
      .withPrettyPrint(true)
      .withModuleKind(ModuleKind.CommonJSModule)
      //.withRelativizeSourceMapBase(Some(fastOptJSURI))
    },
    scalaJSLinkerConfig in (Compile, fullOptJS) ~= { _.withSourceMap(false) },

    libraryDependencies ++= Settings.clientDependencies.value,

    // Create scalajs-react bindings
    stFlavour := Flavour.Japgolly,
    //useYarn := true,

    stEnableScalaJsDefined := Selection.AllExcept("@material-ui/core"),
    stIgnore ++= List("@material-ui/icons", "csstype"),

    Compile / npmDependencies ++= Settings.npmDeps,
    Compile / npmDevDependencies ++= Settings.npmDevDeps,

    // Use a custom config file to export the JS dependencies to the global namespace,
    // as expected by the scalajs-react facade
    webpackConfigFile := Some(baseDirectory.value / "webpack.config.js"),

    // Use a custom config file to export the JS dependencies to the global namespace,
    // as expected by the scalajs-react facade
//    webpackConfigFile in fastOptJS := Some(baseDirectory.value / "dev.webpack.config.js"),
//    webpackConfigFile in fullOptJS := Some(baseDirectory.value / "prod.webpack.config.js"),


    // https://github.com/scalacenter/scalajs-bundler/issues/111
    version in webpack := "4.28.1",
    version in startWebpackDevServer := "3.1.14",
    npmDevDependencies in Compile ++= npmDevOverrides,
    npmResolutions in Compile := npmDevOverrides.toMap,

    // Uniformises fastOptJS/fullOptJS output file name
    artifactPath in(Compile, fastOptJS) := ((crossTarget in(Compile, fastOptJS)).value / "app.js"),
    artifactPath in(Test, fastOptJS) := ((crossTarget in(Test, fastOptJS)).value / "app.test.js"),
    artifactPath in(Compile, fullOptJS) := ((crossTarget in(Compile, fullOptJS)).value / "app.min.js"),
    artifactPath in(Test, fullOptJS) := ((crossTarget in(Test, fullOptJS)).value / "app.min.test.js"),
  )
  .enablePlugins(ScalaJSBundlerPlugin,ScalaJSWeb,SbtWeb)
  .enablePlugins(ScalablyTypedConverterPlugin)
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