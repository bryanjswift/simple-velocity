package velocity

import java.io.{PrintWriter, StringWriter}
import javax.servlet.http.{HttpServletResponse => Response}
import org.apache.velocity.VelocityContext
import scala.collection.JavaConversions

class VelocityView(path:String) {
	val template = VelocityHelper.getTemplate(path)
	implicit def createVelocityContext(model:Map[String,Any]):VelocityContext = {
		val context = new VelocityContext()
		model.foreach(t => {
			val (key,value) = t
			val toPut = value match {
				case s:Seq[_] =>
					JavaConversions.asList(s)
				case i:Iterable[_] =>
					JavaConversions.asIterable(i)
				case _ =>
					value
			}
			context.put(key,toPut)
		})
		context
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
