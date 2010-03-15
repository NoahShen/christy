package com.google.code.christy.c2s.webc2s.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.code.christy.c2s.webc2s.WebC2SManager;

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
		server.setHandler(contexts);
		
		ServletContextHandler root = new ServletContextHandler(contexts, "/", ServletContextHandler.SESSIONS);
		root.addServlet(new ServletHolder(new WebC2sControllerServlet()), "/webc2scontroller.do");
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { root, new DefaultHandler() });
		server.setHandler(handlers);
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
	
	private class WebC2sControllerServlet extends HttpServlet
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -600065750573589411L;

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
		{
			// TODO Auto-generated method stub
			doPost(req, resp);
		}

		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
		{
			String name = (String) req.getParameter("name");
			String callback = (String) req.getParameter("jsoncallback");
			JSONObject jsonObj = new JSONObject();
			try
			{
				jsonObj.put("name", name);
				resp.getWriter().write(callback + "(" + jsonObj.toString() + ")");
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			JSONTokener tokener = new JSONTokener(req.getReader());
//			try
//			{
//				JSONObject jsonObj = new JSONObject(tokener);
//				// TODO
//				System.out.println(jsonObj.toString());
//			}
//			catch (JSONException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
	}

}
