package velocity.resource.loader

import javax.servlet.{GenericServlet,ServletConfig,ServletContext,ServletRequest => Request,ServletResponse => Response}
import velocity.VelocityHelper

class WebappResourceLoaderStartupServlet extends GenericServlet {
	override def init(config:ServletConfig) = {
		super.init(config)
		VelocityHelper.setAttribute(classOf[ServletContext].getName,getServletContext)
	}
	override def service(request:Request,response:Response) = { }
}
