package com.google.code.christy.c2s.defaultc2s.controller;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


import com.caucho.hessian.server.HessianServlet;
import com.google.code.christy.c2s.defaultc2s.C2SManagerImpl;
import com.google.code.christy.lib.ServerController;

public class DefaultC2sController
{
	private C2SManagerImpl c2sManager;
	
	private Server server;

	private int port = 7878;
	
	public DefaultC2sController(C2SManagerImpl c2sManager)
	{
		super();
		this.c2sManager = c2sManager;
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
		
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		
		ServletContextHandler hessianServletContext = new ServletContextHandler(contexts, "/", ServletContextHandler.SESSIONS);
		
		DefaultC2sControllerServlet controllerServlet = new DefaultC2sControllerServlet();
		ServletHolder sh = new ServletHolder(controllerServlet);
		sh.setName("HessianServlet");
		hessianServletContext.addServlet(sh, "/hessianController");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("home-class", DefaultC2sControllerServlet.class.getName());
		params.put("home-api", ServerController.class.getName());
		
		hessianServletContext.setInitParams(params);
		
		server.setHandler(contexts);
		server.start();
		
	}
	
	public void stop() throws Exception
	{
		if (server != null && server.isRunning())
		{
			server.stop();
			server = null;
		}
	}
	
	private class DefaultC2sControllerServlet extends HessianServlet implements ServerController
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -5073227310206149497L;

		@Override
		public Map<String, Object> getServerInfo()
		{
			Map<String, Object> serverInfo = new HashMap<String, Object>();
			
			serverInfo.put("clientLimit", c2sManager.getClientLimit());
			serverInfo.put("domain", c2sManager.getDomain());
			serverInfo.put("name", c2sManager.getName());
			serverInfo.put("routerIp", c2sManager.getRouterIp());
			serverInfo.put("routerPassword", c2sManager.getRouterPassword());
			serverInfo.put("routerPort", c2sManager.getRouterPort());
			serverInfo.put("xmppClientPort", c2sManager.getXmppClientPort());
			serverInfo.put("started", c2sManager.isStarted());
			serverInfo.put("sessionCount", c2sManager.getSessionCount());
			
			return serverInfo;
		}

		@Override
		public void stopServer()
		{
			c2sManager.exit();
		}

		@Override
		public Map<String, Object> execCommand(Map<String, Object> params)
		{
			// TODO Auto-generated method stub
			return null;
			
		}
		
	}

}
