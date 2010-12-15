package velocity.resource.loader

import javax.servlet.{ServletContext,ServletContextEvent,ServletContextListener}
import velocity.VelocityHelper

class WebappResourceLoaderContextListener extends ServletContextListener {
  def contextInitialized(sce:ServletContextEvent):Unit = {
		VelocityHelper.setAttribute(classOf[ServletContext].getName,sce.getServletContext)
  }
  def contextDestroyed(sce:ServletContextEvent):Unit = { }
}
