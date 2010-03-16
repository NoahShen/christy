package com.google.code.christy.c2s.defaultc2s.controller;

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

import com.google.code.christy.c2s.defaultc2s.C2SManagerImpl;

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
		server.setHandler(contexts);
		
		ServletContextHandler root = new ServletContextHandler(contexts, "/", ServletContextHandler.SESSIONS);
		root.addServlet(new ServletHolder(new WebC2sControllerServlet()), "/defaultc2scontroller.do");
		
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
			String type = (String) req.getParameter("type");
			String callback = (String) req.getParameter("jsoncallback");
			JSONObject jsonObj = null;
			if ("set".equals(type))
			{
				
			}
			else if ("get".equals(type))
			{
				jsonObj = handleGet(req);
			}
			
			try
			{
				if (jsonObj != null && jsonObj.length() > 0)
				{
					resp.getWriter().write(callback + "(" + jsonObj.toString() + ")");
				}		
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			
		}

		private JSONObject handleGet(HttpServletRequest req)
		{
			JSONObject jsonObj = new JSONObject();
			
			String requestFields = (String) req.getParameter("requestFields");
			if (requestFields != null)
			{
				String[] fields = requestFields.split(";");
				for (String field : fields)
				{
					try
					{
						if ("started".equals(field))
						{
							jsonObj.put("started", c2sManager.isStarted());
						}
						else if ("domain".equals(field))
						{
							jsonObj.put("domain", c2sManager.getDomain());
						}
						else if ("clientLimit".equals(field))
						{
							jsonObj.put("clientLimit", c2sManager.getClientLimit());
						}
						else if ("sessionCount".equals(field))
						{
							jsonObj.put("sessionCount", c2sManager.getSessionCount());
						}
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			
			return jsonObj;
		}
		
	}

}
