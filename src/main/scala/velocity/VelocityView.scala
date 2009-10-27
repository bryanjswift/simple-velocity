package velocity

import java.util.Properties
import javax.servlet.http.{HttpServletResponse => Response}
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine

object VelocityHelper {
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
	private lazy val engine = new VelocityEngine(properties)
	private val properties = new Properties()
	properties.load(getClass.getClassLoader.getResourceAsStream("velocity/velocity.properties"))
	def getTemplate(template:String) = engine.getTemplate(template)
	def createVelocityContext(model:Map[String,Any]) = {
		val context = new VelocityContext()
		model.foreach(pair =>
			if (pair._2.isInstanceOf[Iterable[_]])
				context.put(pair._1, new IterableWrapper(pair._2.asInstanceOf[Iterable[_]]))
			else
				context.put(pair._1, pair._2)
		)
		context
	}
}

class VelocityView(path:String) {
	private val template = VelocityHelper.getTemplate(path)
	require(template.process,"Error processing or initializing")
	def render(model:Map[String,Any],response:Response):Unit =
		template.merge(VelocityHelper.createVelocityContext(model),response.getWriter())
}
