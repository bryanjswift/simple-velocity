package velocity

import java.io.{PrintWriter, StringWriter}
import java.lang.Object
import javax.servlet.http.{HttpServletResponse => Response}
import java.util.{Map => JMap}
import org.apache.velocity.VelocityContext
import scala.collection.JavaConversions

class VelocityView(path:String) {
	val template = VelocityHelper.getTemplate(path)
	implicit def createVelocityContext(model:scala.collection.Map[String,Any]):VelocityContext = {
		val context = new VelocityContext()
		model.foreach(t => {
			val (key,value) = t
			val toPut = value match {
				case s:Seq[_] =>
					JavaConversions.asJavaList(s)
				case i:Iterable[_] =>
					JavaConversions.asJavaIterable(i)
				case _ =>
					value
			}
			context.put(key,toPut)
		})
		context
	}
	implicit def createVelocityContext(model:JMap[String,Object]):VelocityContext =
		createVelocityContext(JavaConversions.asScalaMap(model))
	def render(model:JMap[String,Object], response:Response):Unit = {
		response.setCharacterEncoding("UTF-8")
		template.merge(model, response.getWriter())
	}
	def render(model:Map[String,Any],response:Response):Unit = {
		response.setCharacterEncoding("UTF-8")
		template.merge(model,response.getWriter())
	}
	def merge(model:Map[String,Any]):String = {
		val swriter = new StringWriter()
		val writer = new PrintWriter(swriter)
		template.merge(model,writer)
		swriter.toString
	}
}
