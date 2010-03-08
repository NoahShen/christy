/**
 * 
 */
package com.google.code.christy.c2s.webc2s;


import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.c2s.C2SManager;
import com.google.code.christy.c2s.ChristyStreamFeature;
import com.google.code.christy.c2s.ClientSession;
import com.google.code.christy.c2s.UnauthorizedException;
import com.google.code.christy.c2s.UnsupportedMechanismException;
import com.google.code.christy.c2s.ChristyStreamFeature.SupportedType;
import com.google.code.christy.mina.XmppCodecFactory;
import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.util.AbstractPropertied;
import com.google.code.christy.util.StringUtils;
import com.google.code.christy.xmpp.Auth;
import com.google.code.christy.xmpp.Failure;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.Packet;
import com.google.code.christy.xmpp.StreamFeature;
import com.google.code.christy.xmpp.Success;
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
	
	private Map<String, WebClientSession> webClientSessions = new ConcurrentHashMap<String, WebClientSession>();
	
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
	
	private int maxWait = 60;
	
	private int minWait = 10;
	
	private int inactivity = 10;
	
	private Server server;

	private SocketConnector routerConnector;
	
	private IoSession routerSession;
	
	private SessionMonitor sessionMonitor;
	
	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;
	
	private XmppParserServiceTracker xmppParserServiceTracker;
	
	private ChristyStreamFeatureServiceTracker streamFeatureStracker;
	
	private UserAuthenticatorTracker userAuthenticatorTracker;
	
	public WebC2SManager(RouteMessageParserServiceTracker routeMessageParserServiceTracker,
				XmppParserServiceTracker xmppParserServiceTracker,
				ChristyStreamFeatureServiceTracker streamFeatureStracker,
				UserAuthenticatorTracker userAuthenticatorTracker)
	{
		super();
		sessionMonitor = new SessionMonitor();
		this.routeMessageParserServiceTracker = routeMessageParserServiceTracker;
		this.xmppParserServiceTracker = xmppParserServiceTracker;
		this.streamFeatureStracker = streamFeatureStracker;
		this.userAuthenticatorTracker = userAuthenticatorTracker;
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
	}

	@Override
	public synchronized void start()
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
		started = true;
		logger.info("webc2s started");
		
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
		root.addServlet(new ServletHolder(new XmppServlet()), getPathSpec());
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { root, resource_handler, new DefaultHandler() });
		server.setHandler(handlers);
		server.start();
		
		sessionMonitor.setStop(false);
		sessionMonitor.start();
		
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
	public synchronized void stop()
	{
		if (!isStarted())
		{
			return;
		}
		if (routerSession != null)
		{
			routerSession.close();
		}
		routerConnector = null;
		
		webClientSessions.clear();
		sessionMonitor.setStop(true);
		started = false;
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


	public int getMaxWait()
	{
		return maxWait;
	}

	public void setMaxWait(int maxWait)
	{
		this.maxWait = maxWait;
	}

	public int getMinWait()
	{
		return minWait;
	}

	public void setMinWait(int minWait)
	{
		this.minWait = minWait;
	}

	public int getInactivity()
	{
		return inactivity;
	}

	public void setInactivity(int inactivity)
	{
		this.inactivity = inactivity;
	}
	
	void addWebClientSession(WebClientSession webClientSession)
	{
		if (webClientSessions.containsKey(webClientSession.getStreamId()))
		{
			throw new RuntimeException("stream id duplication");
		}
		webClientSessions.put(webClientSession.getStreamId(), webClientSession);
	}
	
	void removeWebClientSession(WebClientSession webClientSession)
	{
		webClientSessions.remove(webClientSession.getStreamId());
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
			String streamId = routeMessage.getStreamId();
			if (streamId != null)
			{
				WebClientSession webClientSession = webClientSessions.get(streamId);
				if (webClientSession != null)
				{
					if (routeMessage.isCloseStream())
					{
						webClientSession.setProperty("closed");
					}
					else
					{
						webClientSession.write(routeMessage.getXmlStanza());
					}
					Continuation continuation = webClientSession.getContinuation();
					if (continuation != null)
					{
						continuation.resume();
					}
					
				}
			}
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
	
	private class XmppServlet extends HttpServlet
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -7805269515985886024L;

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
		{
			// TODO Auto-generated method stub
			super.doGet(req, resp);
		}

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
			Continuation continuation = ContinuationSupport.getContinuation(request);
			if (continuation.isInitial())
			{
				response.setContentType("text/xml;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				
				XmlPullParser parser = new MXParser();
				Body body = null;
				try
				{
					parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
					parser.setInput(request.getReader());
					parser.next();
					
					String elementName = parser.getName();
					if (!"body".equals(elementName))
					{
						response.setContentType("text/html;charset=UTF-8");
						response.sendError(HttpServletResponse.SC_NOT_FOUND, "root element must be body");
						return;
					}
					
					body = new Body();
					for (int i = 0; i < parser.getAttributeCount(); i++)
					{
						String attributeName = parser.getAttributeName(i);
						if (!"xmlns".equals(attributeName))
						{
							body.setProperty(attributeName, parser.getAttributeValue(i));
						}
					}
					
					
					boolean done = false;
					while (!done)
					{
						int eventType = parser.next();
						elementName = parser.getName();
						if (eventType == XmlPullParser.START_TAG)
						{
							if (!"body".equals(elementName))
							{
								body.addStanza(xmppParserServiceTracker.getParser().parseParser(parser));
							}
						}
						else if (eventType == XmlPullParser.END_TAG)
						{
							if ("body".equals(elementName))
							{
								done = true;
							}
						}
					}
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.setContentType("text/html;charset=UTF-8");
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, " parsing message error");
					return;
				}
				
				if (!body.containsProperty("sid"))
				{
					createNewSession(request, response, body);
					return;
				}
				
				String sid = (String) body.getProperty("sid");
				
				WebClientSession webClientSession = webClientSessions.get(sid);
				if (webClientSession == null)
				{
					Body responseBody = new Body();
					responseBody.setProperty("type", "terminate");
					responseBody.setProperty("condition", "item-not-found");
					response.getWriter().write(responseBody.toXml());
					return;
				}
				
				if (!checkKey(webClientSession, body))
				{
					Body responsebody = new Body();
					responsebody.setProperty("type", "terminate");
					responsebody.setProperty("condition", "bad-request");
					webClientSession.write(responsebody, response, (String) body.getProperty("rid"));
					webClientSession.close();
					return;
				}
				
				boolean handled = false;
				for (XmlStanza stanza : body.getStanzas())
				{
					if (handleStanza(webClientSession, body, stanza, response))
					{
						handled = true;
					}
				}
				String type = (String) body.getProperty("type");
				if ("terminate".equals(type))
				{
					RouteMessage routeMessage = new RouteMessage(getName(), webClientSession.getStreamId());
					routeMessage.setToUserNode(webClientSession.getUsername());
					routeMessage.setCloseStream(true);
					routerSession.write(routeMessage.toXml());
					
					Body responseBody = new Body();
					responseBody.setProperty("type", "terminate");
					webClientSession.write(responseBody, response, (String) body.getProperty("rid"));
					webClientSession.close();
					return;
				}
					
				if (!handled)
				{
					
					if (webClientSession.hasMessage())
					{
						Body responsebody = new Body();
						responsebody.setProperty("sid", webClientSession.getStreamId());
						for (XmlStanza stanza : webClientSession.getAllMessage())
						{
							if (stanza instanceof StreamFeature)
							{
								responsebody.setProperty("xmlns:stream", "http://etherx.jabber.org/streams");
							}
							responsebody.addStanza(stanza);
						}
						boolean closed = webClientSession.containsProperty("closed");
						if (closed)
						{
							responsebody.setProperty("type", "terminate");
						}
						
						webClientSession.write(responsebody, response, (String) body.getProperty("rid"));
						if (closed)
						{
							webClientSession.close();
						}
						
						return;
					}
					
					continuation.setTimeout(webClientSession.getWait() * 1000);
					webClientSession.setContinuation(continuation);
					continuation.setAttribute("webClientSession", webClientSession);
					continuation.setAttribute("rid", body.getProperty("rid"));
					continuation.suspend();
					
				}
			}
			else
			{
				WebClientSession webClientSession = (WebClientSession) continuation.getAttribute("webClientSession");
				if (webClientSession != null)
				{
					Body responsebody = new Body();
					responsebody.setProperty("sid", webClientSession.getStreamId());
					if (webClientSession.hasMessage())
					{
						for (XmlStanza stanza : webClientSession.getAllMessage())
						{
							if (stanza instanceof StreamFeature)
							{
								responsebody.setProperty("xmlns:stream", "http://etherx.jabber.org/streams");
							}
							responsebody.addStanza(stanza);
						}
					}
					boolean closed = webClientSession.containsProperty("closed");
					if (closed)
					{
						responsebody.setProperty("type", "terminate");
					}
					webClientSession.write(responsebody, response, (String) continuation.getAttribute("rid"));
					if (closed)
					{
						webClientSession.close();
					}
					webClientSession.setContinuation(null);
				}
			}
			
		}

		private boolean checkKey(WebClientSession webClientSession, Body body)
		{
			if (webClientSession.getLastKey() == null)
			{
				String newKey = (String) body.getProperty("newkey");
				if (newKey != null)
				{
					webClientSession.setLastKey(newKey);
				}
				return true;
			}
			
			String key = (String) body.getProperty("key");
			if (key == null)
			{
				return false;
			}
			
			String hashedKey = StringUtils.hash(key);
			if (hashedKey.equals(webClientSession.getLastKey()))
			{
				
				String newKey = (String) body.getProperty("newkey");
				if (newKey != null)
				{
					webClientSession.setLastKey(newKey);
				}
				else
				{
					webClientSession.setLastKey(key);
				}
			}
			else
			{
				return false;
			}
			
			return true;
		}

		private boolean handleStanza(WebClientSession webClientSession, Body body, XmlStanza stanza, HttpServletResponse response) throws IOException
		{
			if (stanza instanceof Auth)
			{
				return handleAuth(webClientSession, body, (Auth) stanza, response);
			}
			else if (stanza instanceof Packet)
			{
				return handlePacket(webClientSession, body, (Packet) stanza, response);
			}
			return false;
		}

		private boolean handlePacket(WebClientSession webClientSession, Body body, Packet packet, HttpServletResponse response)
		{
			// TODO check case sentity
			if (packet.getFrom() != null)
			{
				JID from = packet.getFrom();
				packet.setFrom(new JID(from.getNodePreped(), from.getDomainPreped(), from.getResourcePreped()));
			}
			
			if (packet.getTo() != null) 
			{
				JID to = packet.getTo();
				packet.setTo(new JID(to.getNodePreped(), to.getDomainPreped(), to.getResourcePreped()));
			}
			
			
			RouteMessage routeMessage = new RouteMessage(getName(), webClientSession.getStreamId());
			routeMessage.setToUserNode(webClientSession.getUsername());
			routeMessage.setXmlStanza(packet);

			routerSession.write(routeMessage.toXml());
			return false;
		}

		private boolean handleAuth(WebClientSession webClientSession, Body body, Auth auth, HttpServletResponse response) throws IOException
		{
			String mechanism = auth.getMechanism();
			String content = auth.getContent();
			String rid = (String) body.getProperty("rid");
			try
			{
				userAuthenticatorTracker.authenticate(webClientSession, content, mechanism);
				webClientSession.setStatus(ClientSession.Status.authenticated);
				
				Success success = new Success();
				webClientSession.write(success, response, rid);
				
				ChristyStreamFeature[] features = streamFeatureStracker.getStreamFeatures(SupportedType.afterAuth);
				StreamFeature streamFeature = new StreamFeature();
				for (ChristyStreamFeature feature : features)
				{
					streamFeature.addFeature(feature.getElementName(), feature.getNamespace(), feature.isRequired());
				}
				
				webClientSession.write(streamFeature);
				
			}
			catch (UnauthorizedException e)
			{
				e.printStackTrace();
				
				Failure failure = new Failure();
				failure.setNamespace(Failure.SASL_FAILURE_NS);
				webClientSession.write(failure, response, rid);
				
				Body responsebody = new Body();
				responsebody.setProperty("type", "terminate");
				webClientSession.write(responsebody, response, (String) body.getProperty("rid"));
				
				webClientSession.write(responsebody, response, rid);
				webClientSession.close();
				
				return true;
			}
			catch (UnsupportedMechanismException e)
			{
				e.printStackTrace();
				Failure failure = new Failure();
				failure.setError(Failure.Error.invalid_mechanism);
				failure.setNamespace(Failure.SASL_FAILURE_NS);
				webClientSession.write(failure, response, rid);
				
				Body responsebody = new Body();
				responsebody.setProperty("type", "terminate");
				webClientSession.write(responsebody, response, (String) body.getProperty("rid"));
				
				webClientSession.write(responsebody, response, rid);
				webClientSession.close();
				
				return true;
			}
			
			return true;
		}

		private void createNewSession(HttpServletRequest request, HttpServletResponse response, Body body) throws IOException
		{
			if (!body.containsProperty("rid"))
			{
				response.setContentType("text/html;charset=UTF-8");
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "need rid attribute");
				return;
			}
			
			String domain = (String) body.getProperty("to");
			if (domain == null || !domain.equals(getDomain()))
			{
				response.setContentType("text/html;charset=UTF-8");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "domain error");
				return;
			}
			
			String ver = (String) body.getProperty("ver");
			if (ver == null || !"1.6".equals(ver))
			{
				response.setContentType("text/html;charset=UTF-8");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ver must be 1.6");
				return;
			}
			
			String hold = (String) body.getProperty("hold");
			if (hold == null || !"1".equals(hold))
			{
				response.setContentType("text/html;charset=UTF-8");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "hold must be 1");
				return;
			}
			
			String waitStr = (String) body.getProperty("wait");
			if (waitStr == null)
			{
				response.setContentType("text/html;charset=UTF-8");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "wait can not be null");
				return;
			}
			
			int wait = Integer.valueOf(waitStr);
			if (wait < getMinWait() || wait > getMaxWait())
			{
				response.setContentType("text/html;charset=UTF-8");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "wait must be between " + getMinWait() + " to " + getMaxWait());
				return;
			}
			
			String streamId = nextStreamId();
			WebClientSession webClientSession = new WebClientSession(streamId, WebC2SManager.this, wait);
			webClientSession.setStatus(WebClientSession.Status.connected);
			webClientSession.setAck("1".equals(body.getProperty("ack")));
			if (body.containsProperty("newkey"))
			{
				webClientSession.setLastKey((String) body.getProperty("newkey"));
			}
			sendFeature(request, response, webClientSession, (String) body.getProperty("rid"), SupportedType.afterConnected);
		}

		private void sendFeature(HttpServletRequest request, HttpServletResponse response, 
				WebClientSession webClientSession, String rid, SupportedType type) throws IOException
		{
			ChristyStreamFeature[] features = streamFeatureStracker.getStreamFeatures(type);
			StreamFeature streamFeature = new StreamFeature();
			for (ChristyStreamFeature feature : features)
			{
				streamFeature.addFeature(feature.getElementName(), feature.getNamespace(), feature.isRequired());
			}
			
			if (type != SupportedType.afterAuth)
			{
				String[] mechanisms = userAuthenticatorTracker.getAllMechanisms();
				for (String mech : mechanisms)
				{
					streamFeature.addMechanism(mech);
				}
			}
			
			Body body = new Body();
			body.setProperty("inactivity", getInactivity());
			body.setProperty("requests", "2");
			body.setProperty("sid", webClientSession.getStreamId());
			body.setProperty("wait", webClientSession.getWait());
			body.setProperty("xmlns:stream", "http://etherx.jabber.org/streams");
			
			body.addStanza(streamFeature);
			webClientSession.write(body, response, rid);
		}

		
		@Override
		public void destroy()
		{
			// TODO Auto-generated method stub
			super.destroy();
		}

		@Override
		public void init() throws ServletException
		{
			// TODO Auto-generated method stub
			super.init();
		}
		
	}

	private class SessionMonitor extends Thread
	{
		public static final int SLEEP = 1000;
		
		private boolean stop = true;
		
		public boolean isStop()
		{
			return stop;
		}

		public void setStop(boolean stop)
		{
			this.stop = stop;
		}

		@Override
		public void run()
		{
			while (!isStop())
			{
				WebClientSession[] sessions = webClientSessions.values().toArray(new WebClientSession[]{});
				for (WebClientSession webClientSession : sessions)
				{
					if (!webClientSession.isSuspended()
							&& System.currentTimeMillis() - webClientSession.getLastActive() > inactivity * 1000)
					{
						webClientSession.close();
					}
				}
				
				try
				{
					Thread.sleep(SLEEP);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
