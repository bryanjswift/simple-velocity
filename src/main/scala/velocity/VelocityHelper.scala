package velocity

import java.io.{File, FileInputStream}
import java.util.Properties
import org.apache.velocity.app.VelocityEngine

object VelocityHelper {
	private class EngineIntializedException(message:String) extends RuntimeException
	private var initialized = false
	private var applicationAttributes = Map[Object,Object]()
	private val ClassLoader = getClass().getClassLoader()
	private lazy val properties = new Properties()
	private lazy val engine = loadEngine
	private def stream(path:String) = {
		val f = new File(path)
		if (f.exists) {
			new FileInputStream(f)
		} else {
			ClassLoader.getResourceAsStream(path)
		}
	}
	private def loadEngine = {
		if (!initialized) load("velocity/velocity.properties")
		val engine = new VelocityEngine()
		applicationAttributes.foreach(pair => engine.setApplicationAttribute(pair._1,pair._2))
		engine.init(properties)
		engine
	}
	def load(path:String) =
		if (initialized) {
			throw new EngineIntializedException("VelocityEngine has already been initialized")
		} else {
			properties.load(stream(path))
			initialized = true
		}
	def getTemplate(template:String) = engine.getTemplate(template)
	def setAttribute(key:Object,value:Object) =
		applicationAttributes += (key -> value)
}

