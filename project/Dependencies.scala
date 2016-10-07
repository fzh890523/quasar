package quasar.project

import scala.collection.Seq

import sbt._, Keys._

object Dependencies {
  private val argonautVersion   = "6.2-M3"
  private val doobieVersion     = "0.3.0"
  private val http4sVersion     = "0.14.1a"
  private val jawnVersion       = "0.9.0"
  private val jacksonVersion    = "2.4.4"
  private val monocleVersion    = "1.2.2"
  private val nettyVersion      = "4.1.3.Final"
  private val pathyVersion      = "0.2.2"
  private val raptureVersion    = "2.0.0-M6"
  private val refinedVersion    = "0.5.0"
  private val scalazVersion     = "7.2.4"
  private val scodecBitsVersion = "1.1.0"
  private val shapelessVersion  = "2.3.1"
  private val slcVersion        = "0.4"
  private val scalacheckVersion = "1.12.5"
  private val specsVersion      = "3.8.4-scalacheck-1.12.5"

  def ygg = Seq(
    "org.mapdb"      %  "mapdb"             % "3.0.1",
    "org.spire-math" %% "spire-macros"      % "0.12.0",
    "org.specs2"     %% "specs2-scalacheck" % specsVersion % Test
  )

  def foundation = Seq(
    "org.spire-math"             %% "jawn-parser"               % jawnVersion,
    "org.spire-math"             %% "jawn-ast"                  % jawnVersion,
    "org.threeten"               %  "threetenbp"                % "1.3.2",
    "org.scalaz"                 %% "scalaz-core"               % scalazVersion force(),
    "org.scalaz"                 %% "scalaz-concurrent"         % scalazVersion,
    "org.scalaz"                 %% "scalaz-iteratee"           % scalazVersion,
    "org.scalaz.stream"          %% "scalaz-stream"             % "0.8.3a",
    "com.github.julien-truffaut" %% "monocle-core"              % monocleVersion,
    "com.github.julien-truffaut" %% "monocle-state"             % monocleVersion,
    "com.github.julien-truffaut" %% "monocle-generic"           % monocleVersion,
    "com.github.julien-truffaut" %% "monocle-macro"             % monocleVersion,
    "ai.x"                       %% "lens"                      % "1.0.0",
    "io.argonaut"                %% "argonaut"                  % argonautVersion,
    "io.argonaut"                %% "argonaut-jawn"             % argonautVersion,
    "io.argonaut"                %% "argonaut-scalaz"           % argonautVersion,
    "org.typelevel"              %% "shapeless-scalaz"          % slcVersion,
    "com.slamdata"               %% "matryoshka-core"           % "0.11.1",
    "com.slamdata"               %% "pathy-core"                % pathyVersion,
    "com.slamdata"               %% "pathy-argonaut"            % pathyVersion           % Test,
    "eu.timepit"                 %% "refined"                   % refinedVersion,
    "com.chuusai"                %% "shapeless"                 % shapelessVersion,
    "org.scalacheck"             %% "scalacheck"                % scalacheckVersion      % Test force(),
    "com.github.mpilquist"       %% "simulacrum"                % "0.8.0"                % Test,
    "org.typelevel"              %% "discipline"                % "0.5"                  % Test,
    "org.specs2"                 %% "specs2-core"               % specsVersion           % Test,
    "org.scalaz"                 %% "scalaz-scalacheck-binding" % scalazVersion          % Test,
    "org.typelevel"              %% "shapeless-scalacheck"      % slcVersion             % Test,
    "org.typelevel"              %% "scalaz-specs2"             % "0.4.0"                % Test
  )
  def core = Seq(
    "com.github.tototoshi"       %% "scala-csv"      %    "1.3.1",
    "org.http4s"                 %% "http4s-core"    % http4sVersion,
    "com.slamdata"               %% "pathy-argonaut" %  pathyVersion
  )
  def interface = Seq(
    "com.github.scopt" %% "scopt" % "3.5.0",
    "org.jboss.aesh"    % "aesh"  % "0.66.8"
  )
  def mongodb = Seq(
    "org.mongodb" % "mongodb-driver-async" %   "3.2.2",
    "io.netty"    % "netty-buffer"         % nettyVersion,
    "io.netty"    % "netty-handler"        % nettyVersion
  )
  val postgresql = Seq(
    "org.tpolecat" %% "doobie-core"               % doobieVersion % "compile, test",
    "org.tpolecat" %% "doobie-contrib-postgresql" % doobieVersion % "compile, test"
  )
  def sparkcore = Seq(
    "io.netty"          %  "netty-all"  % nettyVersion,
    ("org.apache.spark" %% "spark-core" % "1.6.2")
      .exclude("commons-collections", "commons-collections")
      .exclude("commons-beanutils", "commons-beanutils-core")
      .exclude("commons-logging", "commons-logging")
      .exclude("com.esotericsoftware.minlog", "minlog")
      .exclude("org.spark-project.spark", "unused")
      .exclude("io.netty", "netty-all")
  )
  def marklogicValidation = Seq(
    "eu.timepit" %% "refined" %  refinedVersion
  )
  def marklogic = Seq(
    "com.fasterxml.jackson.core" %  "jackson-core"        % jacksonVersion,
    "com.fasterxml.jackson.core" %  "jackson-databind"    % jacksonVersion,
    "com.fasterxml.uuid"         %  "java-uuid-generator" % "3.1.4",
    "com.marklogic"              %  "marklogic-xcc"       % "8.0.5",
    "org.scala-lang.modules"     %% "scala-xml"           % "1.0.5"
  )
  val couchbase = Seq(
    "com.couchbase.client" %  "java-client" % "2.3.2",
    "org.http4s"           %% "http4s-core" % http4sVersion
  )
  def web = Seq(
    "org.scodec"     %% "scodec-scalaz"       %     "1.3.0a",
    "org.scodec"     %% "scodec-bits"         % scodecBitsVersion,
    "org.http4s"     %% "http4s-dsl"          %   http4sVersion,
    "org.http4s"     %% "http4s-argonaut62"   %   http4sVersion,
    "org.http4s"     %% "http4s-blaze-server" %   http4sVersion,
    "org.http4s"     %% "http4s-blaze-client" %   http4sVersion    % Test,
    "com.propensive" %% "rapture-json"        %   raptureVersion   % Test,
    "com.propensive" %% "rapture-json-json4s" %   raptureVersion   % Test,
    "eu.timepit"     %% "refined-scalacheck"  %   refinedVersion   % Test
  )
}
