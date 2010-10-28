package velocity

import javax.servlet.http.{HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.velocity.VelocityContext
import scala.collection.JavaConversions

class VelocityView(path:String) {
	val template = VelocityHelper.getTemplate(path)
	def createVelocityContext(model:Map[String,Any],request:Request,response:Response) = {
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
	def render(model:Map[String,Any],request:Request,response:Response):Unit = {
		val context = createVelocityContext(model,request,response)
		response.setCharacterEncoding("UTF-8")
		template.merge(context,response.getWriter())
	}
}
