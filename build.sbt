scalacOptions ++= Seq(
  "-Ywarn-unused-import",
  "-language:postfixOps",
  "-Ypartial-unification"
)

filterScalaLibrary := false                   // include scala library in output
dependencyDotFile := file("dependencies.dot") //render dot file to `./dependencies.dot`

val SparkV   = "3.2.0"
val KafkaV   = ""
val CirceV   = ""
val AkkaV    = "2.6.8"
val AkkaHttp = "10.2.7"

val configDeps = Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.17.1"
)

val loggingDeps = Seq(
  "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2",
  "ch.qos.logback"              % "logback-classic" % "1.2.3",
  "org.codehaus.janino"         % "janino"          % "3.1.0",
  "de.siegmar"                  % "logback-gelf"    % "2.2.0"
).map(
  _.excludeAll(
    ExclusionRule("log4j"),
    ExclusionRule("slf4j-log4j12")
  )
)

val sparkDeps = Seq("org.apache.spark" %% "spark-core" % SparkV).map(
  _.excludeAll(
    ExclusionRule("log4j"),
    ExclusionRule("slf4j-log4j12")
  )
)

val webDeps = Seq()

val commonDependencies = configDeps ++ loggingDeps

lazy val commonSettings = commonSmlBuildSettings ++ Seq(
  organization := "com.pawelzabczynski",
  scalaVersion := "2.13.2",
  libraryDependencies ++= commonDependencies
)

lazy val rootProject = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "big-data-getting-started"
  )
  .aggregate(spark, kafka, web, realProject)

lazy val spark: Project = (project in file("spark"))
  .settings(
    Compile / mainClass := Some("com.pawelzabczynski.SparkApp"),
    libraryDependencies ++= sparkDeps
  )
  .settings(commonSettings)
  .settings(Revolver.settings)

lazy val kafka: Project = (project in file("kafka"))
  .settings(
    Compile / mainClass := Some("com.pawelzabczynski.KafkaApp"),
    libraryDependencies ++= sparkDeps
  )
  .settings(commonSettings)
  .settings(Revolver.settings)

lazy val web: Project = (project in file("web"))
  .settings(
    Compile / mainClass := Some("com.pawelzabczynski.WebApp"),
    libraryDependencies ++= webDeps
  )
  .settings(commonSettings)
  .settings(Revolver.settings)

lazy val realProject: Project = (project in file("real-project"))
  .settings(commonSettings)
  .settings(Revolver.settings)
