package velocity

import java.util.Properties
import javax.servlet.http.{HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.velocity.{Template,VelocityContext}
import org.apache.velocity.app.VelocityEngine

object VelocityHelper {
	private val properties = new Properties()
	properties.load(getClass.getClassLoader.getResourceAsStream("velocity/velocity.properties"))
	private val engine = new VelocityEngine(properties)
	def getTemplate(template:String) = engine.getTemplate(template)
}

class VelocityView(path:String) {
	private class IterableWrapper[T](iterable:Iterable[T]) extends java.lang.Iterable[T] {
		def iterator = new java.util.Iterator[T] {
			private val delegate = iterable.elements
			def hasNext = delegate.hasNext
			def next = delegate.next
			def remove = {
				throw new UnsupportedOperationException
			}
		}
	}
	val template:Template = VelocityHelper.getTemplate(path)
	def createVelocityContext(model:Map[String,Any],request:Request,response:Response) = {
		val context = new VelocityContext()
		model.foreach(pair =>
			if (pair._2.isInstanceOf[Iterable[_]])
				context.put(pair._1, new IterableWrapper(pair._2.asInstanceOf[Iterable[_]]))
			else
				context.put(pair._1, pair._2)
		)
		context
	}
	def render(model:Map[String,Any],request:Request,response:Response):Unit = {
		val context = createVelocityContext(model,request,response)
		response.setCharacterEncoding("UTF-8")
		template.merge(context,response.getWriter())
	}
}
