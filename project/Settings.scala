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
  val version = "0.0.1-SNAPSHOT"

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


  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scalaTags = "0.6.7"
    val scalaDom = "0.9.6"

    val scalajsReactFacade = "1.3.1"
    val diode = "1.1.4"
    val diodeReact = "1.1.4.131"

    val scalajsReactBridge = "0.7.0"
    val scalajsReactComponents = "1.0.0-M2"

    val scalaCSS = "0.5.5"

    val typedApi = "0.2.0"

    val bootstrapFacade = "2.3.5"
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
    "io.circe"              %% "circe-java8"          % CirceVersion,
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
    "com.payalabs"  %%% "scalajs-react-bridge"  % versions.scalajsReactBridge,

    "org.scalacheck"        %% "scalacheck"           % ScalaCheckVersion % Test,
    "org.scalatest"         %% "scalatest"            % ScalaTestVersion  % Test
  ))

  /** Dependencies only used by the ScalaJS client (note the use of %%% instead of %%) */
  val clientDependencies = Def.setting(Seq(
    "com.github.pheymann"               %%% "typedapi-js-client"        % versions.typedApi,
    "com.github.japgolly.scalajs-react" %%% "core"                      % versions.scalajsReactFacade, // withSources (),
    "com.github.japgolly.scalajs-react" %%% "extra"                     % versions.scalajsReactFacade,
    "com.olvind"                        %%% "scalajs-react-components"  % versions.scalajsReactComponents,
    "com.payalabs"                      %%% "scalajs-react-bridge"      % versions.scalajsReactBridge,

    "com.github.japgolly.scalacss"      %%% "core"                      % versions.scalaCSS,
    "com.github.japgolly.scalacss"      %%% "ext-react"                 % versions.scalaCSS,

    "io.suzaku"                         %%% "diode"                     % versions.diode,
    "io.suzaku"                         %%% "diode-react"               % versions.diodeReact,

    "org.scala-js"                      %%% "scalajs-dom"               % versions.scalaDom,

    // Facades of other JavaScript libraries
    "com.github.karasiq"                %%% "scalajs-bootstrap-v4"      % versions.bootstrapFacade,

    "org.scalacheck"                    %%% "scalacheck"                % ScalaCheckVersion % Test,
    "org.scalatest"                     %%% "scalatest"                 % ScalaTestVersion  % Test
  ))

  val npmDeps = Seq(
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

    "@fortawesome/fontawesome-free" -> "5.7.2",

    "react-bootstrap" -> "1.0.0-beta.5",
    "bootstrap" -> "4.1.1",
    "jquery" -> "3.2.1",
    "popper.js" -> "1.14.6",

    "log4javascript" ->  "1.4.15",

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
  )

  val npmDevDeps = Seq(
    // Webpack Loaders for CSS and more
    "webpack-merge" -> "4.1.0",
    "css-loader" -> "0.28.9",
    "postcss-loader" -> "^2.1.1",
    "precss" -> "^3.1.2",
    "file-loader" -> "1.1.6",
    "node-sass" -> "4.9.2",
    "sass-loader" -> "6.0.7",
    "style-loader" -> "0.20.0",
    "url-loader" -> "0.6.2"
  )
}
