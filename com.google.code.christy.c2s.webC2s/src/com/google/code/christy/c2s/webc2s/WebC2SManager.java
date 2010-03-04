/**
 * 
 */
package com.google.code.christy.c2s.webc2s;


import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.SSLFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jabber.JabberHTTPBind.JHBServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.c2s.C2SManager;
import com.google.code.christy.mina.XmppCodecFactory;
import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.util.AbstractPropertied;
import com.google.code.christy.util.StringUtils;
import com.google.code.christy.xmpp.XmlStanza;

/**
 * @author noah
 *
 */
public class WebC2SManager extends AbstractPropertied implements C2SManager
{

	private static String prefix = StringUtils.randomString(10) + "-";

	private static long id = 0;

	public static synchronized String nextStreamId()
	{
		return prefix + Long.toString(id++);
	}
	
	private final Logger logger = LoggerFactory.getLogger(WebC2SManager.class);
	
	
	private int clientLimit = 0;
	
	private String domain;
	
	private String hostName;
	
	private String name;
	
	private String routerIp;
	
	private String routerPassword;
	
	private int routerPort = 8787;
	
	private boolean started = false;

	private boolean routerConnected = false;
	
	private String contextPath  = "/webclient";
	
	private String pathSpec = "/JHB.do";
	
	private String resourceBase = "xmppWebClient/";

	private int webclientPort = 8080;
	
	private Server server;

	private SocketConnector routerConnector;
	
	private IoSession routerSession;
	
	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;
	
	private XmppParserServiceTracker xmppParserServiceTracker;
	
	
	public WebC2SManager(RouteMessageParserServiceTracker routeMessageParserServiceTracker,
				XmppParserServiceTracker xmppParserServiceTracker)
	{
		super();
		this.routeMessageParserServiceTracker = routeMessageParserServiceTracker;
		this.xmppParserServiceTracker = xmppParserServiceTracker;
	}

	@Override
	public void exit()
	{
		stop();
		System.exit(0);
	}

	@Override
	public int getClientLimit()
	{
		return clientLimit;
	}

	@Override
	public String getDomain()
	{
		return domain;
	}

	@Override
	public String getHostName()
	{
		return hostName;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getRouterIp()
	{
		return routerIp;
	}

	@Override
	public String getRouterPassword()
	{
		return routerPassword;
	}

	@Override
	public int getRouterPort()
	{
		return routerPort;
	}

	@Override
	public boolean isRouterConnected()
	{
		return routerConnected;
	}

	@Override
	public boolean isStarted()
	{
		return started;
	}

	@Override
	public void setClientLimit(int clientLimit)
	{
		this.clientLimit = clientLimit;
	}

	@Override
	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	@Override
	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public void setRouterIp(String routerIp)
	{
		this.routerIp = routerIp;
	}

	@Override
	public void setRouterPassword(String routerPassword)
	{
		this.routerPassword = routerPassword;
	}

	@Override
	public void setRouterPort(int routerPort)
	{
		this.routerPort = routerPort;
		int i = ~routerPort;
	}

	@Override
	public void start()
	{
		if (isStarted())
		{
			throw new IllegalStateException("c2s has started");
		}
		
		logger.info("webc2s starting...");
		
		if (getName() == null || getName().isEmpty())
		{
			logger.error("name has not been set");
			throw new IllegalStateException("name has not been set");
		}
		
		if (getDomain() == null || getDomain().isEmpty())
		{
			logger.error("domain has not been set");
			throw new IllegalStateException("domain has not been set");
		}
		
		if (getRouterIp() == null || getRouterIp().isEmpty())
		{
			logger.error("routerIp has not been set");
			throw new IllegalStateException("routerIp has not been set");
		}
		
		if (getRouterPassword() == null || getRouterPassword().isEmpty())
		{
			logger.error("routerPassword has not been set");
			throw new IllegalStateException("routerPassword has not been set");
		}
		
		try
		{
			connect2Router();
			startService();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			started = false;
			logger.error("webc2s starting failure:" + e.getMessage());
			exit();
			return;
		}
		
		
	}

	private void startService() throws Exception
	{
		logger.info("starting http server");
		
		server = new Server(getWebclientPort());

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		server.setHandler(contexts);

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setWelcomeFiles(new String[] { "index.html" });
		resource_handler.setResourceBase(getResourceBase());

		ServletContextHandler root = new ServletContextHandler(contexts, getContextPath(), ServletContextHandler.SESSIONS);
		root.addServlet(new ServletHolder(new JHBServlet()), getPathSpec());

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { root, resource_handler, new DefaultHandler() });
		server.setHandler(handlers);
		server.start();
		
		
		logger.info("server start successful");
	}

	private void connect2Router() throws Exception
	{
		logger.info("connecting to router");
		
		SocketConnectorConfig socketConnectorConfig = new SocketConnectorConfig();
		socketConnectorConfig.setConnectTimeout(30);
		socketConnectorConfig.getFilterChain().addLast("xmppCodec", new ProtocolCodecFilter(new XmppCodecFactory()));
		socketConnectorConfig.setThreadModel(ThreadModel.MANUAL);
		
		routerConnector = new SocketConnector(Runtime.getRuntime().availableProcessors() + 1, Executors.newCachedThreadPool());
		InetSocketAddress address =new InetSocketAddress(getRouterIp(), getRouterPort());
		
		ConnectFuture future = routerConnector.connect(address, new RouterHandler(), socketConnectorConfig);
		if (!future.join(30 * 1000) || !future.isConnected())
		{
			throw new Exception("connecting to router failure");
		}
		
		logger.info("connecting to router successful");
	}

	@Override
	public void stop()
	{
		// TODO Auto-generated method stub

	}

	public String getContextPath()
	{
		return contextPath;
	}

	public void setContextPath(String contextPath)
	{
		this.contextPath = contextPath;
	}

	public String getPathSpec()
	{
		return pathSpec;
	}

	public void setPathSpec(String pathSpec)
	{
		this.pathSpec = pathSpec;
	}

	public String getResourceBase()
	{
		return resourceBase;
	}

	public void setResourceBase(String resourceBase)
	{
		this.resourceBase = resourceBase;
	}

	public int getWebclientPort()
	{
		return webclientPort;
	}

	public void setWebclientPort(int webclientPort)
	{
		this.webclientPort = webclientPort;
	}


	private class RouterHandler implements IoHandler
	{
		

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception
		{
			logger.debug("session" + session + ": exceptionCaught:" + cause.getMessage());
			cause.printStackTrace();
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception
		{
			logger.debug("session" + session + ": messageReceived:\n" + message);
			
			String xml = message.toString();
			if (xml.equals("</stream:stream>"))
			{
				session.close();
				return;
			}
			
			StringReader strReader = new StringReader(xml);
			XmlPullParser parser = new MXParser();
			parser.setInput(strReader);

			try
			{
				parser.nextTag();
			}
			catch (Exception e)
			{
				// e.printStackTrace();
				throw e;
			}

			String elementName = parser.getName();
			if ("stream".equals(elementName) || "stream:stream".equals(elementName))
			{
				handleStream(parser, session);
			}
			else if ("success".equals(elementName))
			{
				routerSession = session;
				routerConnected = true;
				logger.info("router auth successful");
			}
			else if ("failed".equals(elementName))
			{
				logger.error("password error");
				session.close();
			}
			else if ("route".equals(elementName))
			{
				RouteMessage routeMessage = 
					routeMessageParserServiceTracker.getRouteMessageParser().parseXml(xml);
				handleRoute(routeMessage, session);
			}
		}

		private void handleRoute(RouteMessage routeMessage, IoSession session)
		{
//			String streamId = routeMessage.getStreamId();
//			if (streamId != null)
//			{
//				ClientSessionImpl clientSession = clientSessions.get(streamId);
//				if (clientSession != null)
//				{
//					if (routeMessage.isCloseStream())
//					{
//						clientSession.write(CloseStream.getCloseStream());
//						clientSession.setProperty("sessionCleared");
//						clientSession.close();
//					}
//					else
//					{
//						clientSession.write(routeMessage.getXmlStanza());
//					}
//					
//				}
//			}
		}

		private void handleStream(XmlPullParser parser, IoSession session)
		{
			String xmlns = parser.getAttributeValue("", "xmlns");
			String from = parser.getAttributeValue("", "from");
			String id = parser.getAttributeValue("", "id");
			
			if (!C2SROUTER_NAMESPACE.equals(xmlns))
			{
				logger.error("namespace error:" + xmlns);
				session.close();
				return;
			}
			if (!"router".equals(from))
			{
				logger.error("from error:" + from);
				session.close();
				return;
			}
			session.setAttribute("streamId", id);
			
			logger.debug("open stream successful");
			
			session.write("<internal xmlns='" + C2SROUTER_AUTH_NAMESPACE + "'" +
						" c2sname='" + getName() + "' password='" + getRouterPassword() + "'/>");
		}

		@Override
		public void messageSent(IoSession session, Object message) throws Exception
		{
			if (logger.isDebugEnabled())
			{
				String s = null;
				if (message instanceof String)
				{
					s = message.toString();
				}
				else if (message instanceof XmlStanza)
				{
					s = ((XmlStanza)message).toXml();
				}
				logger.debug("session" + session + ": messageSent:\n" + s);
			}
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception
		{
			logger.debug("session" + session + ": sessionClosed");
			exit();
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception
		{
			logger.debug("session" + session + ": sessionCreated");
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception
		{
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception
		{
			logger.debug("session" + session + ": sessionOpened");
			
			session.write("<stream:stream xmlns='" + C2SROUTER_NAMESPACE + "'" +
						" xmlns:stream='http://etherx.jabber.org/streams'" +
						" to='router'" +
						" domain='" + getDomain() + "'>");
		}
		
	}
	
}
