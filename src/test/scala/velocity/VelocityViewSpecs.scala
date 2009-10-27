package velocity

import java.io.{PrintWriter,StringWriter}
import org.specs.Specification

object VelocityViewSpecs extends Specification {
	class Request extends DummyRequest { }
	class Response extends DummyResponse {
		val writer = new StringWriter()
		override def getWriter():PrintWriter = new PrintWriter(writer)
    override def setCharacterEncoding(encoding:String) = { }
	}
	val objTestPath = "templates/objTest.vm"
	val listTestPath = "templates/listTest.vm"

	"Creating a VelocityView with a valid template path" should {
		"produce a non-null view" >> {
			new VelocityView(objTestPath) must notBeNull
		}
	}
	"When rendering a view (" + objTestPath + ") with a simple object (obj), the view" should {
		val view = new VelocityView(objTestPath)
		val response = new Response()
		val obj = new Object {
			val test = "Tester.test"
			override def toString = "Tester.toString"
		}
		view.render(Map("obj" -> obj),response)
		val result = response.writer.toString
		"contain the value of obj.toString" >> {
			result indexOf obj.toString must beGreaterThanOrEqualTo(0)
		}
		"contain the value of obj.test" >> {
			result indexOf obj.test must beGreaterThanOrEqualTo(0)
		}
	}
	"When rendering a view (" + listTestPath + ") with a model containing a list, the view" should {
		val view = new VelocityView(listTestPath)
		val response = new Response()
		val list = List("list test","list test2","another test")
		view.render(Map("tests" -> list),response)
		val result = response.writer.toString
		"contain the first string" >> {
			result must notEqualIgnoreSpace("")
			result indexOf list(0) must beGreaterThanOrEqualTo(0)
		}
		"contain all of the strings" >> {
			result must notEqualIgnoreSpace("")
			list.foreach(s => {
				result indexOf s must beGreaterThanOrEqualTo(0)
			})
		}
	}
}
