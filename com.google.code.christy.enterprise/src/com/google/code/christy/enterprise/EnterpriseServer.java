package com.google.code.christy.enterprise;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.code.christy.log.LoggerServiceTracker;

public class EnterpriseServer
{
	private LoggerServiceTracker loggerServiceTracker;
	
	private Server server;
	
	private String contextPath  = "/webclient";
	
	private String resourceBase = "xmppWebClient/";
	
	private int port = 10007;
	
	public EnterpriseServer(LoggerServiceTracker loggerServiceTracker)
	{
		this.loggerServiceTracker = loggerServiceTracker;
	}

	public String getContextPath()
	{
		return contextPath;
	}

	public void setContextPath(String contextPath)
	{
		this.contextPath = contextPath;
	}

	public String getResourceBase()
	{
		return resourceBase;
	}

	public void setResourceBase(String resourceBase)
	{
		this.resourceBase = resourceBase;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public void start() throws Exception
	{
		server = new Server(getPort());
		
		ServletContextHandler servletContext = new ServletContextHandler(null, getContextPath(), ServletContextHandler.SESSIONS);
		servletContext.addServlet(new ServletHolder(new EnterpriseServlet()), "/enterprise.do");
		servletContext.addServlet(new ServletHolder(new LoginServlet()), "/login.do");
		
//		ResourceHandler resource_handler = new ResourceHandler();
//		resource_handler.setWelcomeFiles(new String[] { "index.html" });
//		resource_handler.setResourceBase(getResourceBase());

		DefaultServlet ds = new DefaultServlet();
		servletContext.addServlet(new ServletHolder(ds), "/");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("org.eclipse.jetty.servlet.Default.resourceBase", getResourceBase());
		
		servletContext.setInitParams(params);
		
		FilterHolder fHolder = new FilterHolder(org.eclipse.jetty.servlets.GzipFilter.class);
		fHolder.setName("compress");
		
		servletContext.addFilter(fHolder, "*.js", FilterMapping.ALL);
		servletContext.addFilter(fHolder, "*.css", FilterMapping.ALL);
		servletContext.addFilter(fHolder, "*.html", FilterMapping.ALL);
		
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { servletContext, new DefaultHandler() });
		server.setHandler(handlers);
		
		server.start();
		server.join();
	}

	public void stop() throws Exception
	{
		if (server != null && server.isStarted())
		{
			server.stop();
			server = null;
		}
	}
	
	private class EnterpriseServlet extends HttpServlet
	{

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
		{

		}

		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
		{
		}

		@Override
		public void destroy()
		{
			super.destroy();
		}

		@Override
		public void init() throws ServletException
		{
			super.init();
		}
	}

}
