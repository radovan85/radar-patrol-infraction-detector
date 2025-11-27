name := """registration-service"""
organization := "com.radovan.play"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.17"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  ehcache,
  ws,
  "jakarta.annotation" % "jakarta.annotation-api" % "3.0.0",
  "jakarta.persistence" % "jakarta.persistence-api" % "3.2.0",
  "org.hibernate.orm" % "hibernate-core" % "7.1.7.Final",
  "com.zaxxer" % "HikariCP" % "7.0.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.20.1",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.20.1",
  "org.mariadb.jdbc" % "mariadb-java-client" % "3.5.5",
  "org.modelmapper" % "modelmapper" % "3.2.5",
  "io.nats" % "jnats" % "2.24.0",
  "io.jsonwebtoken" % "jjwt-api" % "0.12.7",
  "io.jsonwebtoken" % "jjwt-impl" % "0.12.7" % "runtime",
  "io.jsonwebtoken" % "jjwt-jackson" % "0.12.7" % "runtime",
  "com.github.ben-manes.caffeine" % "caffeine" % "3.2.2",
  "io.micrometer" % "micrometer-registry-prometheus" % "1.14.12"

)
