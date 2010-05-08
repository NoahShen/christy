package com.google.code.christy.sm.controller;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.caucho.hessian.server.HessianServlet;
import com.google.code.christy.lib.ServerController;
import com.google.code.christy.sm.impl.SmManagerImpl;

public class SmController
{
	private SmManagerImpl smManager;
	
	private Server server;

	private int port = 7575;
	
	public SmController(SmManagerImpl smManager)
	{
		super();
		this.smManager = smManager;
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
		
		SmControllerServlet controllerServlet = new SmControllerServlet();
		ServletHolder sh = new ServletHolder(controllerServlet);
		sh.setName("HessianServlet");
		hessianServletContext.addServlet(sh, "/hessianController");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("home-class", SmControllerServlet.class.getName());
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
	
	private class SmControllerServlet extends HessianServlet implements ServerController
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 4556979994208220730L;

		@Override
		public Map<String, Object> getServerInfo()
		{
			Map<String, Object> serverInfo = new HashMap<String, Object>();
			
			serverInfo.put("started", smManager.isStarted());
			serverInfo.put("onlineUsersLimit", smManager.getOnlineUsersLimit());
			serverInfo.put("resourceLimitPerUser", smManager.getResourceLimitPerUser());
			serverInfo.put("domain", smManager.getDomain());
			serverInfo.put("name", smManager.getName());
			serverInfo.put("routerIp", smManager.getRouterIp());
			serverInfo.put("routerPassword", smManager.getRouterPassword());
			serverInfo.put("routerPort", smManager.getRouterPort());
			serverInfo.put("onlineUserCount", smManager.getNumberOfOnlineUsers());
			
			return serverInfo;
		}

		@Override
		public void stopServer()
		{
			smManager.exit();
		}
		
	}

}
