import sbt._

class Project(info:ProjectInfo) extends DefaultProject(info) {
  /* repositories */
  val JBossRepo = "JBoss repo" at "http://repository.jboss.org/nexus/content/groups/public"

  /* module configurations */
  val nettyModuleConf = ModuleConfiguration("org.jboss.netty", JBossRepo)
  val scalazModuleConf = ModuleConfiguration("com.googlecode.scalaz", ScalaToolsSnapshots)

  /* dependencies */
  val netty = "org.jboss.netty" % "netty" % "3.2.3.Final" withSources()
  val scalaz_core = "com.googlecode.scalaz" %% "scalaz-core" % "5.1-SNAPSHOT" withSources()
  val scalaz_http = "com.googlecode.scalaz" %% "scalaz-http" % "5.1-SNAPSHOT" withSources()
}