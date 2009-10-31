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
			if (t._2.isInstanceOf[Iterable[_]])
				context.put(t._1, new IterableWrapper(t._2.asInstanceOf[Iterable[_]]))
			else
				context.put(t._1, t._2)
		})
		context
	}
	def render(model:Map[String,Any],request:Request,response:Response):Unit = {
		val context = createVelocityContext(model,request,response)
		response.setCharacterEncoding("UTF-8")
		template.merge(context,response.getWriter())
	}
}
