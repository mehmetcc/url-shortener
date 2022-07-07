ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "me.mehmetcc"
ThisBuild / organizationName := "url-shortener"

val ZioVersion         = "2.0.0-RC5" // for some reason zio-http does not work with definitive zio 2 release
val PureconfigVersion  = "0.17.1"
val ZioHttpVersion     = "2.0.0-RC9"
val RedisClientVersion = "3.42"
val Slf4jVersion       = "1.7.36"
val LogbackVersion     = "1.2.11"
val CirceVersion       = "0.14.2"

lazy val root = (project in file("."))
  .settings(
    name := "url-shortener",
    libraryDependencies ++= Seq(
      "dev.zio"               %% "zio"             % ZioVersion,
      "io.d11"                %% "zhttp"           % ZioHttpVersion,
      "com.github.pureconfig" %% "pureconfig"        % PureconfigVersion,
      "net.debasishg"         %% "redisclient"     % RedisClientVersion,
      "io.circe"              %% "circe-core"      % CirceVersion,
      "io.circe"              %% "circe-generic"   % CirceVersion,
      "io.circe"              %% "circe-parser"    % CirceVersion,
      "ch.qos.logback"         % "logback-classic" % LogbackVersion % Runtime,
      "dev.zio"               %% "zio-test"        % ZioVersion     % Test,
      "io.d11"                %% "zhttp-test"      % ZioHttpVersion % Test
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
