ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "me.mehmetcc"
ThisBuild / organizationName := "example"

val ZioVersion         = "2.0.0"
val PureconfigVersion  = "0.17.1"
val ZioHttpVersion     = "2.0.0-RC9"
val RedisClientVersion = "3.42"

lazy val root = (project in file("."))
  .settings(
    name := "url-shortener",
    libraryDependencies ++= Seq(
      "dev.zio"               %% "zio"         % ZioVersion,
      "com.github.pureconfig" %% "pureconfig"  % PureconfigVersion,
      "io.d11"                %% "zhttp"       % ZioHttpVersion,
      "net.debasishg"         %% "redisclient" % RedisClientVersion,
      "dev.zio"               %% "zio-test"    % ZioVersion     % Test,
      "io.d11"                %% "zhttp-test"  % ZioHttpVersion % Test
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
