package com.google.code.christy.webcontroller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.caucho.hessian.client.HessianProxyFactory;

public class ControllerServlet extends HttpServlet
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3709815613860879833L;

	private Map<String, String> users = new HashMap<String, String>();
	
	private List<ModuleInfo> modules = new ArrayList<ModuleInfo>();

	private HessianProxyFactory factory;
	
	public ControllerServlet()
	{
		factory = new HessianProxyFactory();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		super.destroy();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doPost(req, resp);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		if (username != null && password != null)
		{
			doLogin(req, resp, username, password);
		}

		String action = req.getParameter("action");
		if ("getModuleNodes".equals(action))
		{
			getModulesNodes(req, resp);
		}
		else if ("getModulesInfo".equals(action))
		{
			getModulesInfo(req, resp);
		}
	}

	private void getModulesInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String modules = req.getParameter("modules");
		String[] infos = modules.split(";");
		JSONArray array = new JSONArray();
		for (String info : infos)
		{
			String url = "http://" + info + "/hessianController";
			try
			{
				ServerController controller = (ServerController) factory.create(ServerController.class, url);
				Map<String, Object> serverInfo = controller.getServerInfo();
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", info);
				jsonObj.put("info", serverInfo);
				array.put(jsonObj);
			}
			catch (Exception e)
			{
//					e.printStackTrace();
			}
		}
		resp.getWriter().write(array.toString());
	}

	private void getModulesNodes(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String node = req.getParameter("node");
		if ("moduleRoot".equals(node))
		{
			String defaultc2s = "{'text': 'defaultc2s', 'id': 'defaultc2s', 'cls': 'folder'}";
			String webc2s = "{'text': 'webc2s', 'id': 'webc2s', 'cls': 'folder'}";
			String router = "{'text': 'router', 'id': 'router', 'cls': 'folder'}";
			String sm = "{'text': 'sm', 'id': 'sm', 'cls': 'folder'}";
			
			resp.getWriter().write("[" + defaultc2s + "," + webc2s + "," + router + "," + sm + "]");
		}
		else
		{
			try
			{
				String json = createTreeJson(node);
				resp.getWriter().write(json);
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String createTreeJson(String node) throws JSONException
	{
		JSONArray array = new JSONArray();
		for (ModuleInfo info : modules)
		{
			if (info.getType().equals(node))
			{
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", info.getIp() + ":" + info.getPort());
				jsonObj.put("text", info.getIp() + ":" + info.getPort());
				jsonObj.put("leaf", true);
				jsonObj.put("cls", "file");
				jsonObj.put("ip", info.getIp());
				jsonObj.put("port", info.getPort());
				array.put(jsonObj);
			}
		}
		return array.toString();
	}

	private void doLogin(HttpServletRequest req, HttpServletResponse resp, String username, String password) throws IOException
	{
		if (!users.containsKey(username))
		{
			resp.getWriter().write("{success: false, errors: { reason: 'username not exist' }}");
			return;
		}
		
		String realPass = users.get(username);
		if (!realPass.equals(password))
		{
			resp.getWriter().write("{success: false, errors: { reason: 'password error' }}");
			return;
		}
		
		resp.getWriter().write("{success: true}");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException
	{
		XmlPullParser parser = new MXParser();
		try
		{
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			String appPath = System.getProperty("appPath");
			FileInputStream fis = new FileInputStream(appPath + "/controller.xml");
			parser.setInput(fis, "UTF-8");
			parseConfigFile(parser);
			
		}
		catch (XmlPullParserException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private void parseConfigFile(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("users".equals(elementName))
				{
					parseUser(parser);
				}
				else if ("modules".equals(elementName))
				{
					parseModules(parser);
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("controller".equals(elementName))
				{
					done = true;
				}
			}
		}
	}

	private void parseModules(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				String ip = parser.getAttributeValue("", "ip");
				String port = parser.getAttributeValue("", "port");
				ModuleInfo info = new ModuleInfo(ip, port, elementName);
				modules.add(info);
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("modules".equals(elementName))
				{
					done = true;
				}
			}
		}
	}

	private void parseUser(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				String username = parser.getAttributeValue("", "name");
				String password = parser.getAttributeValue("", "password");
				users.put(username, password);
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("users".equals(elementName))
				{
					done = true;
				}
			}
		}
	}
	
	private class ModuleInfo
	{
		private String ip;
		
		private String port;
		
		private String type;

		/**
		 * @param ip
		 * @param port
		 * @param type
		 */
		public ModuleInfo(String ip, String port, String type)
		{
			this.ip = ip;
			this.port = port;
			this.type = type;
		}

		/**
		 * @return the ip
		 */
		public String getIp()
		{
			return ip;
		}

		/**
		 * @return the port
		 */
		public String getPort()
		{
			return port;
		}

		/**
		 * @return the type
		 */
		public String getType()
		{
			return type;
		}

		/**
		 * @param ip the ip to set
		 */
		public void setIp(String ip)
		{
			this.ip = ip;
		}

		/**
		 * @param port the port to set
		 */
		public void setPort(String port)
		{
			this.port = port;
		}

		/**
		 * @param type the type to set
		 */
		public void setType(String type)
		{
			this.type = type;
		}
		
		
	}

}
