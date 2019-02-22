// Makes our code tidy
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.5.1")

// Revolver allows us to use re-start and work a lot faster!
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

// Native Packager allows us to create standalone jar
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.4")

// Database migrations
addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.2.0")

// Documentation plugins
addSbtPlugin("org.tpolecat" % "tut-plugin" % "0.6.4")

addSbtPlugin("com.47deg"  % "sbt-microsites" % "0.7.18")

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.2")

resolvers += "Flyway".at("https://davidmweber.github.io/flyway-sbt.repo")

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

// ScalaJS and associated plugins
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.26")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")
//addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.8-0.6")
//addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.3")
//addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")

// Extract metadata from sbt and make it available to the code
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")

// https://github.com/rajcspsg/scala_with_cats
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
