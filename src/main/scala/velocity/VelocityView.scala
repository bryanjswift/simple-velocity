package velocity

import collections.IterableWrapper
import javax.servlet.http.{HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.velocity.VelocityContext
import org.apache.velocity.context.Context

class VelocityView(path:String) {
	val template = VelocityHelper.getTemplate(path)
	def createVelocityContext(model:Map[String,Any],request:Request,response:Response) = {
		val context = new VelocityContext()
		model.foreach(t => {
			val (key,value) = t
			val toPut = value match {
				case s:Seq[_] =>
					java.util.Arrays.asList(s.toArray: _*)
				case i:Iterable[_] =>
					new IterableWrapper(value.asInstanceOf[Iterable[_]])
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
