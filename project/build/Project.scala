import sbt._

class Project(info:ProjectInfo) extends DefaultProject(info) {
  /* repositories */
  val JBossRepo = "JBoss repo" at "http://repository.jboss.org/nexus/content/groups/public"

  /* module configurations */
  val nettyModuleConf = ModuleConfiguration("org.jboss.netty", JBossRepo)
  val scalazModuleConf = ModuleConfiguration("org.scalaz", ScalaToolsSnapshots)

  /* dependencies */
  val netty = "org.jboss.netty" % "netty" % "3.2.3.Final" withSources()
  val scalaz_core = "org.scalaz" %% "scalaz-core" % "6.0-SNAPSHOT" withSources()
  val scalaz_http = "org.scalaz" %% "scalaz-http" % "6.0-SNAPSHOT" withSources()
}