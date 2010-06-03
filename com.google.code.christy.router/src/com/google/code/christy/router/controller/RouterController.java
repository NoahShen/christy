package com.google.code.christy.router.controller;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.caucho.hessian.server.HessianServlet;
import com.google.code.christy.lib.ServerController;
import com.google.code.christy.router.impl.RouterManagerImpl;

public class RouterController
{
	private RouterManagerImpl routerManager;
	
	private Server server;

	private int port = 7676;
	
	public RouterController(RouterManagerImpl routerManager)
	{
		super();
		this.routerManager = routerManager;
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
		
		RouterControllerServlet controllerServlet = new RouterControllerServlet();
		ServletHolder sh = new ServletHolder(controllerServlet);
		sh.setName("HessianServlet");
		hessianServletContext.addServlet(sh, "/hessianController");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("home-class", RouterControllerServlet.class.getName());
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
	
	private class RouterControllerServlet extends HessianServlet implements ServerController
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 4556979994208220730L;

		@Override
		public Map<String, Object> getServerInfo()
		{
			Map<String, Object> serverInfo = new HashMap<String, Object>();
			
			serverInfo.put("started", routerManager.isStarted());
			serverInfo.put("c2sLimit", routerManager.getC2sLimit());
			serverInfo.put("c2sPort", routerManager.getC2sPort());
			serverInfo.put("smLimit", routerManager.getSmLimit());
			serverInfo.put("smPort", routerManager.getSmPort());
			serverInfo.put("domain", routerManager.getDomain());
			return serverInfo;
		}

		@Override
		public void stopServer()
		{
			routerManager.exit();
		}

		@Override
		public Map<String, Object> execCommand(Map<String, Object> params)
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
