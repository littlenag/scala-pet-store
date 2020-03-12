import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

/**
 * Application settings. Configure the build for your application here.
 * You normally don't have to touch the actual build definition after this.
 */
object Settings {
  /** The name of your application */
  val name = "scala-pet-store"

  /** The version of your application */
  val version = "0.0.2-SNAPSHOT"

  /** Options for the scala compiler */
  val scalacOptions = Seq(
    // format: off
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
    "-language:higherKinds",             // Allow higher-kinded types
    "-language:implicitConversions",     // Allow definition of implicit functions called views
    "-language:postfixOps",              // Allow postfix operations
    "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
    //"-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
    "-Xfuture",                          // Turn on future language features.
    "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
    "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
    "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
    "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
    "-Xlint:option-implicit",            // Option.apply used implicit view.
    "-Xlint:package-object-classes",     // Class or object defined in package object.
    "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
    "-Xlint:unsound-match",              // Pattern match may not be typesafe.
    "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    "-Ypartial-unification",             // Enable partial unification in type constructor inference
    //"-Ywarn-dead-code",                  // Warn when dead code is identified.
    "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
    "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-numeric-widen",              // Warn when numerics are widened.
    "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals",              // Warn if a local definition is unused.
    //"-Ywarn-unused:params",            // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates",            // Warn if a private member is unused.
    "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
    // format: on
  )

  val CatsVersion            = "1.6.0"
  val CirceVersion           = "0.11.1"
  val CirceConfigVersion     = "0.6.1"
  val DoobieVersion          = "0.6.0"
  //val EnumeratumVersion      = "1.5.13"
  val EnumeratumCirceVersion = "1.5.20"
  val H2Version              = "1.4.197"
  val Http4sVersion          = "0.20.0-M6"
  val LogbackVersion         = "1.2.3"
  val ScalaCheckVersion      = "1.14.0"
  val ScalaTestVersion       = "3.0.5"
  val FlywayVersion          = "5.2.4"
  val TsecVersion            = "0.1.0-M3"
  val CourierVersion         = "1.0.0"


  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scalaTags = "0.6.7"
    val scalaDom = "0.9.6"

    val scalaCSS = "0.5.5"

    val scalajsReactFacade = "1.4.2"
    val diode = "1.1.5"
    val diodeReact = "1.1.4.131"

    val scalajsReactBridge = "0.8.1"

    // react-bootstrap
    val scalajsReactBootstrap = "0.0.6"

    val typedApi = "0.2.0"
  }

  /**
    * Dependencies only used by the Server.
    */
  val serverDependencies = Def.setting(Seq(
    "org.typelevel"         %% "cats-core"            % CatsVersion,
    "io.circe"              %% "circe-generic"        % CirceVersion,
    "io.circe"              %% "circe-literal"        % CirceVersion,
    "io.circe"              %% "circe-generic-extras" % CirceVersion,
    "io.circe"              %% "circe-parser"         % CirceVersion,
    "io.circe"              %% "circe-config"         % CirceConfigVersion,
    "org.tpolecat"          %% "doobie-core"          % DoobieVersion,
    "org.tpolecat"          %% "doobie-h2"            % DoobieVersion,
    "org.tpolecat"          %% "doobie-scalatest"     % DoobieVersion,
    "org.tpolecat"          %% "doobie-hikari"        % DoobieVersion,
    //"com.beachape"          %% "enumeratum"           % EnumeratumVersion,
    "com.beachape"          %% "enumeratum-circe"     % EnumeratumCirceVersion,
    "com.h2database"        %  "h2"                   % H2Version,
    "org.http4s"            %% "http4s-blaze-server"  % Http4sVersion,
    "org.http4s"            %% "http4s-circe"         % Http4sVersion,
    "org.http4s"            %% "http4s-dsl"           % Http4sVersion,
    "ch.qos.logback"        %  "logback-classic"      % LogbackVersion,
    "org.flywaydb"          %  "flyway-core"          % FlywayVersion,
    //"com.github.pureconfig" %% "pureconfig"           % PureConfigVersion,
    "org.http4s"            %% "http4s-blaze-client"  % Http4sVersion     % Test,
    "org.scalacheck"        %% "scalacheck"           % ScalaCheckVersion % Test,
    "org.scalatest"         %% "scalatest"            % ScalaTestVersion  % Test,

    "com.github.pheymann"   %% "typedapi-server"      % versions.typedApi,

    // For sending activation and recovery emails
    "com.github.daddykotex" %% "courier"              % CourierVersion,
    "org.jvnet.mock-javamail" % "mock-javamail"       % "1.9" % "test",

    // Authentication dependencies
    "io.github.jmcardon"    %% "tsec-common"          % TsecVersion,
    "io.github.jmcardon"    %% "tsec-password"        % TsecVersion,
    "io.github.jmcardon"    %% "tsec-mac"             % TsecVersion,
    "io.github.jmcardon"    %% "tsec-signatures"      % TsecVersion,
    "io.github.jmcardon"    %% "tsec-jwt-mac"         % TsecVersion,
    "io.github.jmcardon"    %% "tsec-jwt-sig"         % TsecVersion,
    "io.github.jmcardon"    %% "tsec-http4s"          % TsecVersion
  ))

  /**
    * These dependencies are shared between JS and JVM projects
    * the special %%% function selects the correct version for each project
    */
  val sharedDependencies = Def.setting(Seq(
    "com.github.pheymann" %%% "typedapi-shared"      % versions.typedApi,
    "com.github.pheymann" %%% "typedapi-client"      % versions.typedApi,

    "com.lihaoyi"         %%% "scalatags"             % versions.scalaTags,
    //"com.beachape"        %%% "enumeratum"           % EnumeratumVersion,
    "com.beachape"        %%% "enumeratum-circe"      % EnumeratumCirceVersion,
    "io.circe"            %%% "circe-generic"         % CirceVersion,
    "io.circe"            %%% "circe-literal"         % CirceVersion,
    "io.circe"            %%% "circe-generic-extras"  % CirceVersion,
    "io.circe"            %%% "circe-parser"          % CirceVersion,
    "io.circe"            %%% "circe-java8"           % CirceVersion,

    // Shared so that the JSWriter macro can be compiled separately
    "com.payalabs"        %%% "scalajs-react-bridge"  % versions.scalajsReactBridge,

    "org.scalacheck"      %%% "scalacheck"            % ScalaCheckVersion % Test,
    "org.scalatest"       %%% "scalatest"             % ScalaTestVersion  % Test
  ))

  /** Dependencies only used by the ScalaJS client (note the use of %%% instead of %%) */
  val clientDependencies = Def.setting(Seq(
    "com.github.pheymann"               %%% "typedapi-js-client"        % versions.typedApi,

    "com.github.japgolly.scalajs-react" %%% "core"                      % versions.scalajsReactFacade, // withSources (),
    "com.github.japgolly.scalajs-react" %%% "extra"                     % versions.scalajsReactFacade,
    "com.payalabs"                      %%% "scalajs-react-bridge"      % versions.scalajsReactBridge,

    "com.github.japgolly.scalacss"      %%% "core"                      % versions.scalaCSS,
    "com.github.japgolly.scalacss"      %%% "ext-react"                 % versions.scalaCSS,

    "io.suzaku"                         %%% "diode"                     % versions.diode,
    "io.suzaku"                         %%% "diode-react"               % versions.diodeReact,

    "org.scala-js"                      %%% "scalajs-dom"               % versions.scalaDom,

    //"io.github.littlenag"               %%% "scalajs-react-bootstrap"   % versions.scalajsReactBootstrap,

    "org.scalacheck"                    %%% "scalacheck"                % ScalaCheckVersion % Test,
    "org.scalatest"                     %%% "scalatest"                 % ScalaTestVersion  % Test
  ))

  val npmDeps = Seq(
    "@fortawesome/fontawesome-free" -> "5.7.2",

    //"@material-ui/core" -> "4.9.5",
    "@material-ui/core" -> "3.9.3",
    "typeface-roboto" -> "0.0.75",

    // https://getbootstrap.com/docs/4.3/getting-started/introduction/
    //"react-bootstrap" -> "1.0.0-beta.16",
    //"bootstrap"       -> "4.3.1",
    //"jquery"          -> "3.3",
    //"@types/jquery"   -> "3.3.31",
    //"@popperjs/core"  -> "2.1.0",  //https://popper.js.org/

    "log4javascript"  -> "1.4.15",

    "@types/react"    -> "16.8.4",
    "react"           -> "16.8.4",
    "react-dom"       -> "16.8.4",
  )

  val npmDevDeps = Seq(
    // Webpack Loaders for CSS and more
    "webpack-merge" -> "4.1.0",
    "css-loader" -> "0.28.9",
    "postcss-loader" -> "^2.1.1",
    "precss" -> "^3.1.2",
    "file-loader" -> "4.3.0",
    "node-sass" -> "4.12.0",
    "sass-loader" -> "6.0.7",
    "style-loader" -> "0.20.0",
    "url-loader" -> "0.6.2"
  )
}
