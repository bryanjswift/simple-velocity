import bjs.project.ResolverPlugin
import sbt._

class SimpleVelocityProject(info:ProjectInfo) extends DefaultProject(info) with ResolverPlugin {
	// *-- Compile Dependencies
	// Servlet API
	val servletApi = "javax.servlet" % "servlet-api" % "2.4"
	// Velocity
	val velocity = "org.apache.velocity" % "velocity" % "1.6.2"

	// *-- Test Dependencies
	// Specs
	val junit = "junit" % "junit" % "4.7" % "test->default"
	val specs = "org.scala-tools.testing" % "specs" % "1.6.1-2.8.0.Beta1-RC6" % "test->default"

	// Publish settings
	override def managedStyle = ManagedStyle.Maven
	// Also package sources and docs
	override def packageDocsJar = defaultJarPath("-javadoc.jar")
	override def packageSrcJar= defaultJarPath("-sources.jar")
	val sourceArtifact = Artifact(artifactID, "src", "jar", Some("sources"), Nil, None)
	val docsArtifact = Artifact(artifactID, "docs", "jar", Some("javadoc"), Nil, None)
	override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageDocs, packageSrc)

	// override looking for jars in ./lib
	override def dependencyPath = "src" / "main" / "lib"
	// override path to managed dependency cache
	override def managedDependencyPath = "project" / "lib_managed"
}
