lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.11"

val circeVersion = "0.9.3"
val sttpV = "1.1.13"
fork in Test := true
parallelExecution in Test := false


lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "2.12.4"
    )),
    name := "akka-http-quickstart-scala",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      // Support of CORS requests, version depends on akka-http
      "ch.megard" %% "akka-http-cors" % "0.3.0",

      // SQL generator
      "com.typesafe.slick" %% "slick" % "3.2.3",

      // Postgres driver
      "org.postgresql" % "postgresql" % "42.1.4",
      // Migration for SQL databases
      "org.flywaydb" % "flyway-core" % "4.2.0",

      // Connection pool for database
      "com.zaxxer" % "HikariCP" % "2.7.0",

      // Encoding decoding sugar, used in passwords hashing
      "com.roundeights" %% "hasher" % "1.2.0",

      // Parsing and generating of JWT tokens
      "com.pauldijou" %% "jwt-core" % "0.16.0",

      // Config file parser
      "com.github.pureconfig" %% "pureconfig" % "0.9.1",

      // Sugar for serialization and deserialization in akka-http with circe
      "de.heikoseeberger" %% "akka-http-circe" % "1.20.1",

      // JSON serialization library
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      // Http client, used currently only for IT test
      "com.softwaremill.sttp" %% "core" % sttpV % Test,
      "com.softwaremill.sttp" %% "akka-http-backend" % sttpV % Test,

      // Test
      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test,
      "ru.yandex.qatools.embed" % "postgresql-embedded" % "2.4" % Test,

      "org.scalamock" %% "scalamock" % "4.1.0" % Test
    )
  )
