import sbt.Keys.testForkedParallel

scalacOptions ++= Seq(
  "-Ywarn-unused-import",
  "-language:postfixOps",
  "-Ypartial-unification"
)

filterScalaLibrary := false                   // include scala library in output
dependencyDotFile := file("dependencies.dot") //render dot file to `./dependencies.dot`

val SparkV   = "3.2.0"
val KafkaV   = ""
val CirceV   = "0.13.0"
val AkkaV    = "2.6.8"
val AkkaHttp = "10.2.7"
val TapirV   = "0.17.19"
val SttpV    = "3.3.4"
val Http4sV  = "0.21.23"
val TsecV    = "0.2.1"
val DoobieV  = "0.13.3"
val ZioV     = "1.0.4-2"

val configDeps = Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.17.1"
)

val repositoryResolvers = Seq(
  "twitter-repo" at "https://repo.clojars.org/"
)

val unitTestingStack = Seq(
  "org.scalatest"              %% "scalatest"       % "3.1.1"  % Test,
  "org.scalamock"              %% "scalamock"       % "4.4.0"  % Test,
  "com.opentable.components"    % "otj-pg-embedded" % "0.13.3" % Test,
  "org.flywaydb"                % "flyway-core"     % "6.2.4"  % Test,
  "com.softwaremill.quicklens" %% "quicklens"       % "1.4.12" % Test,
  "io.github.embeddedkafka"    %% "embedded-kafka"  % "3.0.0"
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

val stormDeps = Seq(
  "org.apache.storm" % "storm-core" % "2.3.0",
  "org.apache.storm" % "storm-kafka-client" % "2.3.0"


)

val sparkDeps = Seq("org.apache.spark" %% "spark-core" % SparkV).map(
  _.excludeAll(
    ExclusionRule("log4j"),
    ExclusionRule("slf4j-log4j12")
  )
)

val coreDeps = Seq(
  "io.monix"                %% "monix"           % "3.4.0",
  "com.softwaremill.common" %% "tagging"         % "2.2.1",
  "org.postgresql"           % "postgresql"      % "42.3.1", // just for migration
  "io.github.jmcardon"      %% "tsec-password"   % TsecV,
  "io.github.jmcardon"      %% "tsec-cipher-jca" % TsecV
)

val dbDeps = Seq(
  "org.tpolecat" %% "doobie-core"     % DoobieV,
  "org.tpolecat" %% "doobie-hikari"   % DoobieV,
  "org.tpolecat" %% "doobie-postgres" % DoobieV
)

val jsonDeps = Seq(
  "io.circe" %% "circe-core"    % CirceV,
  "io.circe" %% "circe-generic" % CirceV,
  "io.circe" %% "circe-parser"  % CirceV
)

val commonDependencies = Seq(
) ++ configDeps ++ loggingDeps ++ unitTestingStack ++ coreDeps

val kafkaDeps = Seq(
  "org.apache.kafka" % "kafka-clients"  % "2.1.0",
  "io.monix"        %% "monix-kafka-1x" % "1.0.0-RC6"
)

val webDeps = Seq(
  "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"              % TapirV,
  "com.softwaremill.sttp.tapir"   %% "tapir-openapi-circe-yaml"        % TapirV,
  "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-http4s"         % TapirV,
  "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"                % TapirV,
  "com.softwaremill.sttp.client3" %% "circe"                           % SttpV,
  "org.http4s"                    %% "http4s-dsl"                      % Http4sV,
  "org.http4s"                    %% "http4s-blaze-server"             % Http4sV,
  "org.http4s"                    %% "http4s-blaze-client"             % Http4sV,
  "org.http4s"                    %% "http4s-circe"                    % Http4sV,
  "org.http4s"                    %% "http4s-prometheus-metrics"       % Http4sV,
  "com.softwaremill.sttp.client3" %% "async-http-client-backend-monix" % SttpV,
  "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"             % TapirV,
  "com.softwaremill.sttp.client3" %% "slf4j-backend"                   % SttpV
) ++ jsonDeps ++ kafkaDeps ++ dbDeps

lazy val kafkaUploaderDeps = dbDeps ++ jsonDeps ++ kafkaDeps ++ stormDeps

lazy val commonModuleDeps = jsonDeps ++ kafkaDeps

lazy val commonSettings = commonSmlBuildSettings ++ Seq(
  organization := "com.pawelzabczynski",
  scalaVersion := "2.13.2",
  libraryDependencies ++= commonDependencies,
  resolvers ++= repositoryResolvers,
  testForkedParallel := false
)

lazy val rootProject = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "big-data-getting-started"
  )
  .aggregate(spark, kafkaUploader, web, realProject)

lazy val commons = (project in file("commons"))
  .settings(libraryDependencies := commonModuleDeps)
  .settings(commonSettings)
  .settings(Revolver.settings)

lazy val spark: Project = (project in file("spark"))
  .settings(
    Compile / mainClass := Some("com.pawelzabczynski.SparkApp"),
    libraryDependencies ++= sparkDeps
  )
  .settings(commonSettings)
  .settings(Revolver.settings)

lazy val kafkaUploader: Project = (project in file("kafka-uploader"))
  .settings(
    Compile / mainClass := Some("com.pawelzabczynski.KafkaApp"),
    libraryDependencies ++= kafkaUploaderDeps
  )
  .settings(commonSettings)
  .settings(Revolver.settings)
  .dependsOn(commons)

lazy val web: Project = (project in file("web"))
  .settings(
    Compile / mainClass := Some("com.pawelzabczynski.WebApp"),
    libraryDependencies ++= webDeps
  )
  .settings(commonSettings)
  .settings(Revolver.settings)
  .dependsOn(commons)

lazy val realProject: Project = (project in file("real-project"))
  .settings(commonSettings)
  .settings(Revolver.settings)

// DB migrations
enablePlugins(FlywayPlugin)
flywayUrl := "jdbc:postgresql://localhost:5432/iot"
flywayUser := "postgres"
flywayPassword := "P@55word"
flywayLocations += "filesystem:./migrations/db"
