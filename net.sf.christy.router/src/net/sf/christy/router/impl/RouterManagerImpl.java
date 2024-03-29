package net.sf.christy.router.impl;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import net.sf.christy.mina.XmppCodecFactory;
import net.sf.christy.routemessage.RouteMessage;
import net.sf.christy.router.RouterToSmMessageDispatcher;
import net.sf.christy.router.RouterManager;
import net.sf.christy.router.SmSession;
import net.sf.christy.util.AbstractPropertied;
import net.sf.christy.xmpp.CloseStream;
import net.sf.christy.xmpp.JID;
import net.sf.christy.xmpp.Packet;
import net.sf.christy.xmpp.StreamError;
import net.sf.christy.xmpp.XmlStanza;

/**
 * 
 * @author noah
 *
 */
public class RouterManagerImpl extends AbstractPropertied implements RouterManager
{

	private static int id = 0;

	public static synchronized int nextId()
	{
		return id++;
	}
	
	public static final String C2SROUTER_NAMESPACE = "christy:internal:c2s2router";
	
	public static final String C2SROUTER_AUTH_NAMESPACE = "christy:internal:c2s2router:auth";
	
	public static final String SMROUTER_NAMESPACE = "christy:internal:sm2router";
	
	public static final String SMROUTER_AUTH_NAMESPACE = "christy:internal:sm2router:auth";
	
	private final Logger logger = LoggerFactory.getLogger(RouterManagerImpl.class);

	private IoAcceptor c2sAcceptor;
	
	private boolean started = false;
	
	private int c2sPort = 8787;
	
	private int s2sPort = 8788;
	
	private int smPort = 8789;
	
	private int modulePort = 8790;
	
	private int c2sLimit = 0;

	private int smLimit = 0;
	
	private String domain;
	
	private Map<String, C2sSessionImpl> c2sSessions = new ConcurrentHashMap<String, C2sSessionImpl>();
	
	private Map<String, SmSessionImpl> smSessions = new ConcurrentHashMap<String, SmSessionImpl>();
	
	private Map<String, String> registeredC2sModules = new ConcurrentHashMap<String, String>();

	private Map<String, String> registeredSmModules = new ConcurrentHashMap<String, String>();

	private Map<String, String> registeredS2sModules = new ConcurrentHashMap<String, String>();
	
	private Map<String, String> registeredOtherModules = new ConcurrentHashMap<String, String>();

	private SocketAcceptor smAcceptor;

	private RouterToSmMessageDispatcherTracker dispatcherServiceTracker;

	private RouterToSmInterceptorServiceTracker routerToSmInterceptorServiceTracker;

	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;

	/**
	 * @param resourceBinderServiceTracker
	 * @param routerToSmInterceptorServiceTracker 
	 * @param routeMessageParserServiceTracker 
	 */
	public RouterManagerImpl(RouterToSmMessageDispatcherTracker resourceBinderServiceTracker, 
						RouterToSmInterceptorServiceTracker routerToSmInterceptorServiceTracker, 
						RouteMessageParserServiceTracker routeMessageParserServiceTracker)
	{
		this.dispatcherServiceTracker = resourceBinderServiceTracker;
		this.routerToSmInterceptorServiceTracker = routerToSmInterceptorServiceTracker;
		this.routeMessageParserServiceTracker = routeMessageParserServiceTracker;
	}

	@Override
	public void registerC2sModule(String name, String md5Password)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		registeredC2sModules.put(name, md5Password);
	}

	@Override
	public void registerSmModule(String name, String md5Password)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		registeredSmModules.put(name, md5Password);
	}

	@Override
	public void registerOtherModule(String subDomain, String md5Password)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		registeredOtherModules.put(subDomain, md5Password);
	}

	@Override
	public void unregisterOtherModule(String subDomain)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		registeredOtherModules.remove(subDomain);
	}
	
	@Override
	public void registerS2sModule(String name, String md5Password)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		registeredS2sModules.put(name, md5Password);
	}
	
	@Override
	public void unregisterS2sModule(String name)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		registeredS2sModules.remove(name);
	}
	
	@Override
	public synchronized void exit()
	{
		stop();
		System.exit(0);
	}

	@Override
	public int getC2sLimit()
	{
		return c2sLimit;
	}

	@Override
	public String getDomain()
	{
		return domain;
	}

	@Override
	public int getSmLimit()
	{
		return smLimit;
	}

	@Override
	public void unregisterC2sModule(String name)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		registeredC2sModules.remove(name);
	}

	@Override
	public void unregisterSmModule(String name)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		registeredSmModules.remove(name);
	}

	@Override
	public void setC2sLimit(int c2sLimit)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		if (c2sLimit < 0)
		{
			throw new IllegalArgumentException("c2sLimit must be > 0");
		}
		this.c2sLimit = c2sLimit;
	}

	@Override
	public void setDomain(String domain)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		this.domain = domain;
	}

	@Override
	public void setSmLimit(int smLimit)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		if (smLimit < 0)
		{
			throw new IllegalArgumentException("smLimit must be > 0");
		}
		this.smLimit = smLimit;
	}

	@Override
	public synchronized void start()
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		
		if (getDomain() == null || getDomain().isEmpty())
		{
			logger.error("domain has not been set");
			throw new IllegalStateException("domain has not been set");
		}
		
		if (registeredC2sModules.isEmpty())
		{
			logger.error("c2s has not been registered");
			throw new IllegalStateException("c2s has not been registered");
		}
		
		if (registeredSmModules.isEmpty())
		{
			logger.error("sm has not been registered");
			throw new IllegalStateException("sm has not been registered");
		}
		
//		if (resourceBinderServiceTracker.size() <= 0)
//		{
//			logger.error("no resourceBinder service");
//			throw new IllegalStateException("no resourceBinder service");
//		}
		
		logger.info("router starting...");
		
		try
		{
			startC2sAcceptor();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			logger.error("c2s acceptor start failure:" + e.getMessage());
			return;
		}
		try
		{
			startSmAcceptor();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			logger.error("sm acceptor start failure:" + e.getMessage());
			return;
		}
		
		started = true;
		logger.info("router successful start");
	}

	private void startSmAcceptor() throws IOException
	{
		smAcceptor = new SocketAcceptor();
		IoAcceptorConfig config = new SocketAcceptorConfig();
		DefaultIoFilterChainBuilder chain = config.getFilterChain();

		chain.addFirst("xmppCodec", new ProtocolCodecFilter(new XmppCodecFactory()));
		
		int smPort = getSmPort();
		
		smAcceptor.bind(new InetSocketAddress(smPort), new SmHandler(), config);
	}

	private void startC2sAcceptor() throws IOException
	{
		c2sAcceptor = new SocketAcceptor();
		IoAcceptorConfig config = new SocketAcceptorConfig();
		DefaultIoFilterChainBuilder chain = config.getFilterChain();

		chain.addFirst("xmppCodec", new ProtocolCodecFilter(new XmppCodecFactory()));
		
		int c2sPort = getC2sPort();
		
		c2sAcceptor.bind(new InetSocketAddress(c2sPort), new C2sHandler(), config);
	}

	@Override
	public synchronized void stop()
	{
		if (!isStarted())
		{
			return;
		}
		
		for (C2sSessionImpl c2sSession : c2sSessions.values())
		{
			c2sSession.close();
		}
		c2sSessions.clear();
		if (c2sAcceptor != null)
		{
			c2sAcceptor.unbindAll();
		}
		
		
		for (SmSessionImpl smSession : smSessions.values())
		{
			smSession.close();
		}
		smSessions.clear();
		if (smAcceptor != null)
		{
			smAcceptor.unbindAll();
		}
		
		
		started = false;
	}

	@Override
	public boolean isStarted()
	{
		return started;
	}
	
	@Override
	public int getC2sPort()
	{
		return c2sPort;
	}

	@Override
	public int getSmPort()
	{
		return smPort;
	}

	@Override
	public void setC2sPort(int c2sPort)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		this.c2sPort = c2sPort;
	}

	@Override
	public void setSmPort(int smPort)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		this.smPort = smPort;
	}

	@Override
	public int getS2sPort()
	{
		return s2sPort;
	}

	@Override
	public void setS2sPort(int s2sPort)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		this.s2sPort = s2sPort;
	}
	
	@Override
	public int getModulePort()
	{
		return modulePort;
	}

	@Override
	public void setModulePort(int modulePort)
	{
		if (isStarted())
		{
			throw new IllegalStateException("router has started");
		}
		this.modulePort = modulePort;
	}
	
	void addC2sSession(String c2sname, C2sSessionImpl c2sSession)
	{
		c2sSessions.put(c2sname, c2sSession);
		logger.debug("add new c2sSession:" + c2sname);
	}
	
	void removeC2sSession(String c2sname)
	{
		c2sSessions.remove(c2sname);
		logger.debug("remove c2sSession:" + c2sname);
	}
	
	void addSmSession(String smname, SmSessionImpl smSession)
	{
		smSessions.put(smname, smSession);
		logger.debug("add new smSession:" + smname);
	}
	
	void removeSmSession(String smname)
	{
		smSessions.remove(smname);
		logger.debug("remove smSession:" + smname);
	}
	
	private class C2sHandler implements IoHandler
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
				C2sSessionImpl c2sSession = (C2sSessionImpl) session.getAttachment();
				if (c2sSession == null)
				{
					session.close();
				}
				else
				{
					c2sSession.close();
				}
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
				C2sSessionImpl c2sSession = (C2sSessionImpl) session.getAttachment();
				if (c2sSession == null)
				{
					c2shandleStream(parser, session);
				}
			}
			else if ("internal".equals(elementName))
			{
				c2shandleInternal(parser, session);
			}
			else if ("route".equals(elementName))
			{
				RouteMessage routeMessage = 
					routeMessageParserServiceTracker.getRouteMessageParser().parseParser(parser);
				c2shandleRoute(routeMessage, session);
			}
		}

		private void c2shandleRoute(RouteMessage routeMessage, IoSession session) throws XmlPullParserException, IOException
		{
			RouterToSmMessageDispatcher dispatcher = dispatcherServiceTracker.getDispatcher();
			String userNode = routeMessage.getToUserNode();
			if (userNode != null)
			{
				dispatcher.sendMessage(routeMessage);
			}
			
			
		}

		private void c2shandleInternal(XmlPullParser parser, IoSession session)
		{
			String streamId = session.getAttribute("streamId").toString();
			if (streamId != null)
			{
				String c2sname = parser.getAttributeValue("", "c2sname");
				String password = parser.getAttributeValue("", "password");
				
				if (!c2sname.startsWith("c2s_"))
				{
					StreamError error = new StreamError(StreamError.Condition.undefined_condition);
					session.write(error);
					session.write(CloseStream.getCloseStream());
					session.close();
					return;
				}
				
				if (c2sSessions.containsKey(c2sname))
				{
					StreamError error = new StreamError(StreamError.Condition.conflict);
					session.write(error);
					session.write(CloseStream.getCloseStream());
					session.close();
					return;
				}
				
				if (!registeredC2sModules.containsKey(c2sname))
				{
					StreamError error = new StreamError();
					error.addApplicationCondition("unregistered", C2SROUTER_AUTH_NAMESPACE);
					session.write(error);
					session.write(CloseStream.getCloseStream());
					session.close();
					return;
				}
				
				String registeredPwd = registeredC2sModules.get(c2sname);
				if (password.equals(registeredPwd))
				{
					C2sSessionImpl c2sSession = 
						new C2sSessionImpl(session.getAttribute("streamId").toString(), 
										c2sname, 
										session, 
										RouterManagerImpl.this);
					
					c2sSession.write("<success xmlns='" + C2SROUTER_AUTH_NAMESPACE + "' />");
					return;
				}
				else
				{
					session.write("<failed xmlns='" + C2SROUTER_AUTH_NAMESPACE + "'>" +
								"<reason>password error</reason>" +
								"</failed>");
					session.write(CloseStream.getCloseStream());
					session.close();
					return;
				}
			}
			// not open stream
			else
			{
				session.close();
				return;
			}
		}

		private void c2shandleStream(XmlPullParser parser, IoSession session)
		{
			String xmlns = parser.getAttributeValue("", "xmlns");
			String to = parser.getAttributeValue("", "to");
			String domain = parser.getAttributeValue("", "domain");

			
			if (!xmlns.equals(C2SROUTER_NAMESPACE))
			{
				StreamError error = new StreamError(StreamError.Condition.invalid_namespace);
				session.write(error);
				session.write(CloseStream.getCloseStream());
				session.close();
				return;
			}
			
			if (!getDomain().equals(domain))
			{
				StreamError error = new StreamError();
				error.addApplicationCondition("domain-error", C2SROUTER_NAMESPACE);
				session.write(error);
				session.write(CloseStream.getCloseStream());
				session.close();
				return;
			}
			
			if (!to.equals("router"))
			{
				StreamError error = new StreamError(StreamError.Condition.host_gone);
				session.write(error);
				session.write(CloseStream.getCloseStream());
				session.close();
				return;
			}
			
			String streamId =  "c2s_internalstream_" + nextId();
			session.setAttribute("streamId", streamId);
			String responseStream = "<stream:stream" +
								" xmlns='"+ C2SROUTER_NAMESPACE + "'" +
								" xmlns:stream='http://etherx.jabber.org/streams'" +
								" from='router'" +
								" id='" + streamId + "'>";
			session.write(responseStream);
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
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception
		{
			logger.debug("session" + session + ": sessionCreated");
			if (getC2sLimit() != 0 && c2sSessions.size() == getC2sLimit())
			{
				StreamError error = new StreamError(StreamError.Condition.internal_server_error);
				error.addApplicationCondition("reach-c2s-limit", C2SROUTER_NAMESPACE);
				session.write(error);
				session.write(CloseStream.getCloseStream());
				session.close();
				logger.info("closing session" + session + ": c2sSession limit reached");
			}
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception
		{
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception
		{
			logger.debug("session" + session + ": sessionOpened");
			
		}
		
	}
	
	private class SmHandler implements IoHandler
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
				SmSessionImpl smSession = (SmSessionImpl) session.getAttachment();
				if (smSession == null)
				{
					session.close();
				}
				else
				{
					smSession.close();
				}
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
				SmSessionImpl smSession = (SmSessionImpl) session.getAttachment();
				if (smSession == null)
				{
					smhandleStream(parser, session);
				}
			}
			else if ("internal".equals(elementName))
			{
				smhandleInternal(parser, session);
			}
			else if ("route".equals(elementName))
			{
				RouteMessage routeMessage = 
					routeMessageParserServiceTracker.getRouteMessageParser().parseParser(parser);
				smhandleRoute(routeMessage, session);
			}
			
		}
		
		private void smhandleRoute(RouteMessage routeMessage, IoSession session)
		{
			
			if (routerToSmInterceptorServiceTracker.fireRouteMessageReceived(routeMessage, 
															(SmSession) session.getAttachment()))
			{
				logger.debug("Message which recieved from "
							+ session + "has been intercepted.Message:"
							+ routeMessage.toXml());
				return;
			}

			String userNode = routeMessage.getToUserNode();
			String to = routeMessage.getTo();
			
			if (userNode != null)
			{
				dispatcherServiceTracker.getDispatcher().sendMessage(routeMessage);
			}
			else if (to != null)
			{
				if (to.startsWith("c2s_"))
				{
					C2sSessionImpl c2sSession = c2sSessions.get(to);
					if (c2sSession != null)
					{
						c2sSession.write(routeMessage);
					}
				}
				else if (to.startsWith("sm_"))
				{
					SmSessionImpl smSession = smSessions.get(to);
					if (smSession != null)
					{
						smSession.write(routeMessage);
					}
				}
			}
			else
			{
				XmlStanza stanza = routeMessage.getXmlStanza();
				if (stanza instanceof Packet)
				{
					Packet packet = (Packet) stanza;
					JID jid = packet.getTo();
					if (!jid.getDomain().equals(getDomain()))
					{
						// TODO send to s2s
					}
					else 
					{
						String node = jid.getNode();
						routeMessage.setToUserNode(node);
						
						dispatcherServiceTracker.getDispatcher().sendMessage(routeMessage);
					}
				}
			}
			
		}

		private void smhandleInternal(XmlPullParser parser, IoSession session)
		{
			String streamId = session.getAttribute("streamId").toString();
			if (streamId != null)
			{
				String smName = parser.getAttributeValue("", "smname");
				String password = parser.getAttributeValue("", "password");
				
				if (!smName.startsWith("sm_"))
				{
					StreamError error = new StreamError(StreamError.Condition.undefined_condition);
					session.write(error);
					session.write(CloseStream.getCloseStream());
					session.close();
					return;
				}
				
				if (smSessions.containsKey(smName))
				{
					StreamError error = new StreamError(StreamError.Condition.conflict);
					session.write(error);
					session.write(CloseStream.getCloseStream());
					session.close();
					return;
				}
				
				if (!registeredSmModules.containsKey(smName))
				{
					session.write("<error>" +
								"<unregistered xmlns=\"christy:internal:sm2router:auth\"/>" +
								"</error> ");
					session.write(CloseStream.getCloseStream());
					session.close();
					return;
				}
				
				String registeredPwd = registeredSmModules.get(smName);
				if (password.equals(registeredPwd))
				{
					SmSessionImpl smSession = 
						new SmSessionImpl(streamId, 
										smName, 
										session, 
										RouterManagerImpl.this,
										routerToSmInterceptorServiceTracker);
					session.setAttachment(smSession);
					smSession.write("<success xmlns='" + SMROUTER_AUTH_NAMESPACE + "' />");
					try
					{
						dispatcherServiceTracker.getDispatcher().smSessionAdded(smSession);
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						
						logger.error("resourceBinder add smsession error");
						session.write("<error>" +
									"<internal-error xmlns=\"christy:internal:sm2router:auth\"/>" +
									"</error> ");
						session.write(CloseStream.getCloseStream());
						session.close();
						
					}
					return;
				}
				else
				{
					session.write("<failed xmlns='" + SMROUTER_AUTH_NAMESPACE + "'>" +
								"<reason>password error</reason>" +
								"</failed>");
					session.write(CloseStream.getCloseStream());
					session.close();
					return;
				}
			}
			// not open stream
			else
			{
				session.close();
				return;
			}
		}

		private void smhandleStream(XmlPullParser parser, IoSession session)
		{
			String xmlns = parser.getAttributeValue("", "xmlns");
			String to = parser.getAttributeValue("", "to");
			String domain = parser.getAttributeValue("", "domain");

			
			if (!xmlns.equals(SMROUTER_NAMESPACE))
			{
				StreamError error = new StreamError(StreamError.Condition.invalid_namespace);
				session.write(error);
				session.write(CloseStream.getCloseStream());
				session.close();
				return;
			}
			
			if (!getDomain().equals(domain))
			{
				StreamError error = new StreamError();
				error.addApplicationCondition("domain-error", SMROUTER_NAMESPACE);
				session.write(error);
				session.write(CloseStream.getCloseStream());
				session.close();
				return;
			}
			
			if (!to.equals("router"))
			{
				StreamError error = new StreamError(StreamError.Condition.host_gone);
				session.write(error);
				session.write(CloseStream.getCloseStream());
				session.close();
				return;
			}
			
			String streamId =  "sm_internalstream_" + nextId();
			session.setAttribute("streamId", streamId);
			String responseStream = "<stream:stream" +
								" xmlns='"+ SMROUTER_NAMESPACE + "'" +
								" xmlns:stream='http://etherx.jabber.org/streams'" +
								" from='router'" +
								" id='" + streamId + "'>";
			session.write(responseStream);
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
			Object atta = session.getAttachment();
			if (atta != null || atta instanceof SmSession)
			{
				dispatcherServiceTracker.getDispatcher().smSessionRemoved((SmSession) atta);
			}
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception
		{
			logger.debug("session" + session + ": sessionCreated");

			if (getSmLimit() != 0 && smSessions.size() == getSmLimit())
			{
				StreamError error = new StreamError(StreamError.Condition.internal_server_error);
				error.addApplicationCondition("reach-sm-limit", SMROUTER_NAMESPACE);
				session.write(error);
				session.write(CloseStream.getCloseStream());
				session.close();
				logger.info("closing session" + session + ": smSession limit reached");
			}
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception
		{
			logger.debug("session" + session + ": sessionOpened");
			
		}
		
	}

}
