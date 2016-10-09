name := "slick-test"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Ywarn-dead-code",
  "-Xlint",
  "-Xfatal-warnings",
  "-language:experimental.macros"
)

credentials += Credentials("Sonatype Nexus Repository Manager", "rbs-develop.paymentgate.ru", "s.dikanskiy", "fscebxy2")

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"
resolvers += "RBS thirdparty" at "https://rbs-develop.paymentgate.ru/repo/content/repositories/thirdparty"
resolvers += "Underscore Bintray" at "https://dl.bintray.com/underscoreio/libraries"
resolvers += Resolver.typesafeRepo("releases")

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
  "com.typesafe.slick" %% "slick-extensions" % "3.1.0",
  "com.chuusai"        %% "shapeless" % "2.3.1",
  "io.underscore"      %% "slickless" % "0.2.1",
  "com.h2database" % "h2" % "1.4.191",
  "com.oracle" % "ojdbc6" % "11.2.0.3.0",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

mainClass in (Compile, run) := Some("ru.dsv.slick.Main")

//testOptions in Test += Tests.Argument("-oD")
testOptions in Test += Tests.Argument("-oFD")
testOptions in Test += Tests.Argument("-oFD")