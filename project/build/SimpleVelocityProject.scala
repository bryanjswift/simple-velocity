import sbt._

class SimpleVelocityProject(info:ProjectInfo) extends DefaultProject(info) {
	// *-- Compile Dependencies
	// Servlet API
	val servletApi = "javax.servlet" % "servlet-api" % "2.4"
	// Velocity
	val velocity = "org.apache.velocity" % "velocity" % "1.6.2"

	// *-- Test Dependencies
	// Specs
	val specs = "org.scala-tools.testing" % "specs" % "1.6.0" % "test->default"
	val junit = "junit" % "junit" % "4.5" % "test->default"

	// override looking for jars in ./lib
	override def dependencyPath = "src" / "main" / "lib"
	// override path to managed dependency cache
	override def managedDependencyPath = "project" / "lib_managed"
}
