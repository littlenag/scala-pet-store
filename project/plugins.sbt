// Revolver allows us to use re-start and work a lot faster!
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

// Native Packager allows us to create standalone jar
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.18")

// ScalaJS and associated plugins
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.31")

// ScalablyTyped converter
resolvers += Resolver.bintrayRepo("oyvindberg", "converter")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter06" % "1.0.0-beta5")

// Bundle everything using webpack
addSbtPlugin("ch.epfl.scala" % "sbt-web-scalajs-bundler-sjs06" % "0.16.0")

// Auto copy the compiled client to the server's resources directory
//addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.8-0.6")

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.1")

// Extract metadata from sbt and make it available to the code
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")

// https://github.com/rajcspsg/scala_with_cats
