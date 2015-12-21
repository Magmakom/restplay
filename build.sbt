name := """restplay"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.7.play24",
  "org.reactivemongo" %% "reactivemongo-extensions-json" % "0.11.7.play24",
  "com.iheart" %% "ficus" % "1.2.0",
  "net.codingwell" %% "scala-guice" % "4.0.1",
  "com.adrianhurt" %% "play-bootstrap" % "1.0-P24-B3-SNAPSHOT",
  "com.mohiva" %% "play-silhouette" % "3.0.4",
  "com.mohiva" %% "play-silhouette-testkit" % "3.0.4" % "test",
  specs2 % Test
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
