package com.google.code.christy.c2s.webc2s.controller;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.caucho.hessian.server.HessianServlet;
import com.google.code.christy.c2s.webc2s.WebC2SManager;
import com.google.code.christy.lib.ServerController;

public class WebC2sController
{
	private WebC2SManager webc2sManager;
	
	private Server server;

	private int port = 7777;
	
	public WebC2sController(WebC2SManager webc2sManager)
	{
		super();
		this.webc2sManager = webc2sManager;
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
		
		WebC2sControllerServlet controllerServlet = new WebC2sControllerServlet();
		ServletHolder sh = new ServletHolder(controllerServlet);
		sh.setName("HessianServlet");
		hessianServletContext.addServlet(sh, "/hessianController");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("home-class", WebC2sControllerServlet.class.getName());
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
	
	private class WebC2sControllerServlet extends HessianServlet implements ServerController
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6938050325203554038L;

		@Override
		public Map<String, Object> getServerInfo()
		{
			Map<String, Object> serverInfo = new HashMap<String, Object>();
			
			serverInfo.put("started", webc2sManager.isStarted());
			serverInfo.put("clientLimit", webc2sManager.getClientLimit());
			serverInfo.put("domain", webc2sManager.getDomain());
			serverInfo.put("name", webc2sManager.getName());
			serverInfo.put("routerIp", webc2sManager.getRouterIp());
			serverInfo.put("routerPassword", webc2sManager.getRouterPassword());
			serverInfo.put("routerPort", webc2sManager.getRouterPort());
			serverInfo.put("webClientPort", webc2sManager.getWebclientPort());
			serverInfo.put("sessionCount", webc2sManager.getSessionCount());
			return serverInfo;
		}

		@Override
		public void stopServer()
		{
			webc2sManager.exit();
		}

		@Override
		public Map<String, Object> execCommand(Map<String, Object> params)
		{
			String action = (String) params.get("action");
			if ("close_session".equals(action))
			{
				closeSessionAction(params);
			}
			return null;
		}

		private void closeSessionAction(Map<String, Object> params)
		{
			// TODO Auto-generated method stub
			
		}
		
	}

}
