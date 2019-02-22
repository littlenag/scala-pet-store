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
    "-Ywarn-dead-code",                  // Warn when dead code is identified.
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
  val TsecVersion            = "0.1.0-M2"

  //val Http4sVersion = "0.18.12"
  val utestV = "0.6.2"
  val scalaJsDomV = "0.9.6"
  val scalaTagsV = "0.6.7"
  val circeV = "0.9.3"
  val catsEffectV = "0.10.1"

  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scalaDom = "0.9.6"
    //val scalajsReact = "1.2.3"
    val scalajsReact = "1.3.1"
    val diode = "1.1.4"
    val diodeReact = "1.1.4.131"
    val scalaCSS = "0.5.5"
    val log4js = "1.4.10"
    //val autowire = "0.2.6"
    //val booPickle = "1.3.0"
    val uTest = "0.6.4"
    val typedApi = "0.2.0"

    val react = "16.5.2"
    val jsTokens = "4.0.0"
    //val jQuery = "3.3.1"
    //val bootstrap = "4.1.3"
    //val fontAwesome = "4.3.0-1"
    //val chartjs = "2.7.2"

    val jQueryFacade = "1.2"
    //val jQuery = "1.11.1"
    val jQuery = "2.2.1"
    val bootstrap = "3.3.6"
    val fontAwesome = "4.3.0-1"
    val chartjs = "2.1.3"

    val scalajsScripts = "1.1.2"
  }

  /**
    * Dependencies only used by the Backend project. Includes:
    *   - scala and java deps
    *   - webjars
    */
  val backendDependencies = Def.setting(Seq(
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
    "io.github.jmcardon"    %% "tsec-http4s"          % TsecVersion,


    // FIXME i don't think these need to be here
    "org.webjars.npm"  % "bootstrap"           % versions.bootstrap,
    "org.webjars.npm"  % "react"               % versions.react,
    "org.webjars.npm"  % "react-dom"           % versions.react,
    "org.webjars.npm"  % "js-tokens"           % versions.jsTokens
    //"org.webjars"      % "chartjs"             % "2.1.3"
  ))

  /**
    * These dependencies are shared between JS and JVM projects
    * the special %%% function selects the correct version for each project
    */
  val sharedDependencies = Def.setting(Seq(
    "com.github.pheymann"   %%% "typedapi-client"      % versions.typedApi,

    "com.lihaoyi"   %%% "scalatags"            % Settings.scalaTagsV,
    //"com.lihaoyi"   %%% "autowire"             % versions.autowire,
    //"io.suzaku"     %%% "boopickle"            % versions.booPickle,
    //"com.beachape"  %%% "enumeratum"           % EnumeratumVersion,
    "com.beachape"  %%% "enumeratum-circe"     % EnumeratumCirceVersion,
    "io.circe"      %%% "circe-generic"        % CirceVersion,
    "io.circe"      %%% "circe-literal"        % CirceVersion,
    "io.circe"      %%% "circe-generic-extras" % CirceVersion,
    "io.circe"      %%% "circe-parser"         % CirceVersion,
    "io.circe"      %%% "circe-java8"          % CirceVersion,
  ))

  /** Dependencies only used by the ScalaJS project (note the use of %%% instead of %%) */
  val frontendDependencies = Def.setting(Seq(
    // ScalaJS client support
    "com.github.pheymann"               %%% "typedapi-js-client"   % versions.typedApi,
    "com.github.japgolly.scalajs-react" %%% "core"                 % versions.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "extra"                % versions.scalajsReact,
    "com.github.japgolly.scalacss"      %%% "core"                 % versions.scalaCSS,
    "com.github.japgolly.scalacss"      %%% "ext-react"            % versions.scalaCSS,
    "io.suzaku"                         %%% "diode"                % versions.diode,
    "io.suzaku"                         %%% "diode-react"          % versions.diodeReact,
    //"io.suzaku"                         %%% "boopickle"            % versions.booPickle,
    "org.scala-js"                      %%% "scalajs-dom"          % versions.scalaDom,
    "com.lihaoyi"                       %%% "utest"                % versions.uTest % Test,
    "com.lihaoyi"                       %%% "scalatags"            % Settings.scalaTagsV,
    //"com.lihaoyi"                       %%% "autowire"             % versions.autowire,
    "org.querki"                        %%% "jquery-facade"        % versions.jQueryFacade
  ))

  /** Dependencies for external JS libs that are bundled into a single .js file according to dependency order */
  val jsDependencies = Def.setting(Seq(
    //"org.webjars.npm"   % "js-tokens"      % "4.0.0"            / "js-tokens/4.0.0/index.js" commonJSName "jsTokens",
    "org.webjars.npm"   % "react"          % versions.react     / "umd/react.development.js" minified "umd/react.production.min.js" commonJSName "React",
    "org.webjars.npm"   % "react-dom"      % versions.react     / "umd/react-dom.development.js" minified  "umd/react-dom.production.min.js" dependsOn "umd/react.development.js" commonJSName "ReactDOM",
    "org.webjars.npm"   % "react-dom"      % versions.react     / "umd/react-dom-server.browser.development.js" minified  "umd/react-dom-server.browser.production.min.js" dependsOn "umd/react-dom.development.js" commonJSName "ReactDOMServer",
    "org.webjars"       % "jquery"         % versions.jQuery    / "jquery.js"    minified "jquery.min.js",
    "org.webjars"       % "bootstrap"      % versions.bootstrap / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js",
    //"org.webjars"       % "chartjs"        % versions.chartjs   / "Chart.js"     minified "Chart.min.js",
    "org.webjars"       % "log4javascript" % versions.log4js    / "js/log4javascript_uncompressed.js" minified "js/log4javascript.js"
  ))
}
