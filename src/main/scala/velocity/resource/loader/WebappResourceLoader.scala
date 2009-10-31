package velocity.resource.loader

import collections.VectorWrapper
import java.io.{File,InputStream}
import javax.servlet.ServletContext
import org.apache.commons.collections.ExtendedProperties
import org.apache.velocity.exception.ResourceNotFoundException
import org.apache.velocity.runtime.resource.Resource
import org.apache.velocity.runtime.resource.loader.ResourceLoader
import org.apache.velocity.util.StringUtils

class WebappResourceLoader extends ResourceLoader {
	class StreamAndFile(val stream:InputStream,val file:Option[String])
	private var paths:Array[String] = _
	private var servletContext:ServletContext = _
	private var templates = Map[String,String]()
	def init(configuration:ExtendedProperties) = {
		log.trace("WebappResourceLoader : initialization starting.")
		val vector = new VectorWrapper(configuration.getVector("path").asInstanceOf[java.util.Vector[String]])
		paths = Array("/") ++ vector.map(s => s.trim)
		if (log.isDebugEnabled) {
			for (path <- paths) {
				log.debug("WebappResourceLoader : adding path '" + path + "'")
			}
		}
		val obj = rsvc.getApplicationAttribute(classOf[ServletContext].getName)
		if (obj.isInstanceOf[ServletContext]) {
			servletContext = obj.asInstanceOf[ServletContext]
		} else {
			log.error("WebappResourceLoader: unable to retrieve ServletContext")
		}
		log.trace("WebappResourceLoader : initialization complete.")
	}
	def getResourceStream(source:String):InputStream = {
		val template = StringUtils.normalizePath(source)
		if (template == null || template.length == 0) {
			val msg = "File resource error : argument " + template +
				" contains .. and may be trying to access " +
				"content outside of template root.  Rejected."
			log.error("WebappResourceLoader : " + msg)
			throw new ResourceNotFoundException(msg)
		}
		val saf = findStreamAndFile(template)
		if (!saf.file.isEmpty) {
			templates += template -> saf.file.get
		}
		saf.stream
	}
	private def findStream(path:String,template:String):Option[InputStream] =
		try {
			Some(servletContext.getResourceAsStream(path + trimTemplateName(template)))
		} catch {
			case e:Exception => None
		}
	private def findFile(path:String,template:String):Option[String] = {
		val realPath = servletContext.getRealPath(path + trimTemplateName(template))
		if (realPath == null) { None }
		else { Some(realPath) }
	}
	private def findStreamAndFile(template:String) = {
		val resources =
			for {
				path <- paths
				t = findStream(path,template)
				f = findFile(path,template)
				if (!t.isEmpty)
			} yield new StreamAndFile(t.get,f)
		if (resources.size == 0) {
			throw new ResourceNotFoundException("WebappResourceLoader : cannot find " + template)
		} else {
			resources(0)
		}
	}
	private def getCachedFile(template:String):Option[File] = {
		val path = templates.get(template)
		if (path.isEmpty) { None }
		else { Some(new File(path.get)) }
	}
	private def getFile(template:String):Option[File] = {
		val saf = findStreamAndFile(template)
		saf.file match {
			case Some(filePath) => Some(new File(filePath))
			case None => None
		}
	}
	private def trimTemplateName(name:String) = name.dropWhile(_ == '/').mkString
	def isSourceModified(resource:Resource) = {
		val root = servletContext.getRealPath("/")
		val name = resource.getName
		val cachedFile = getCachedFile(name)
		val file =
			try {
				getFile(name)
			} catch {
				case _ => None
			}
		if (root == null) { false }
		else {
			(file != cachedFile ||
				(!file.eq(cachedFile) && !file.isEmpty && !cachedFile.isEmpty && file.get.lastModified != cachedFile.get.lastModified))
		}
	}
	def getLastModified(resource:Resource) = {
		val root = servletContext.getRealPath("/")
		val name = resource.getName
		val file = getCachedFile(name)
		if (root == null) { 0 }
		else {
			if (file.isEmpty) { 0 }
			else { file.get.lastModified }
		}
	}
}
