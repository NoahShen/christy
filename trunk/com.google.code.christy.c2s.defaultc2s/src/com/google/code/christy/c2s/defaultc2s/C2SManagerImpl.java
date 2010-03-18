package com.google.code.christy.c2s.defaultc2s;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.SSLFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.c2s.C2SManager;
import com.google.code.christy.c2s.ChristyStreamFeature;
import com.google.code.christy.c2s.ClientSession;
import com.google.code.christy.c2s.OpenStreamException;
import com.google.code.christy.c2s.UnauthorizedException;
import com.google.code.christy.c2s.UnsupportedMechanismException;
import com.google.code.christy.c2s.ChristyStreamFeature.SupportedType;
import com.google.code.christy.log.LoggerServiceTracker;
import com.google.code.christy.mina.XmppCodecFactory;
import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.util.AbstractPropertied;
import com.google.code.christy.util.StringUtils;
import com.google.code.christy.xmpp.Auth;
import com.google.code.christy.xmpp.Challenge;
import com.google.code.christy.xmpp.CloseStream;
import com.google.code.christy.xmpp.Failure;
import com.google.code.christy.xmpp.Iq;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.Packet;
import com.google.code.christy.xmpp.Proceed;
import com.google.code.christy.xmpp.StartTls;
import com.google.code.christy.xmpp.Stream;
import com.google.code.christy.xmpp.StreamError;
import com.google.code.christy.xmpp.StreamFeature;
import com.google.code.christy.xmpp.Success;
import com.google.code.christy.xmpp.XmlStanza;


public class C2SManagerImpl extends AbstractPropertied implements C2SManager
{

	private static String prefix = StringUtils.randomString(10) + "-";

	private static long id = 0;

	public static synchronized String nextStreamId()
	{
		return prefix + Long.toString(id++);
	}
	
	private Map<String, ClientSessionImpl> clientSessions = new ConcurrentHashMap<String, ClientSessionImpl>();
	
	private int clientLimit = 0;
	
	private String domain;
	
	private String hostName;
	
	private String name;
	
	private String routerIp;
	
	private String routerPassword;
	
	private int routerPort = 8787;
	
	private int xmppClientPort = 5222;
	
	private boolean started = false;

	private boolean routerConnected = false;
	
	private SocketConnector routerConnector;

	private IoSession routerSession;

	private SocketAcceptor c2sAcceptor;

	private ChristyStreamFeatureServiceTracker streamFeatureStracker;
	
	private TlsContextServiceTracker tlsContextServiceTracker;
	
	private UserAuthenticatorTracker userAuthenticatorTracker;
	
	private XmppParserServiceTracker xmppParserServiceTracker;
	
	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;
	
	private LoggerServiceTracker loggerServiceTracker;
	
	/**
	 * @param streamFeatureStracker
	 */
	public C2SManagerImpl(ChristyStreamFeatureServiceTracker streamFeatureStracker,
						TlsContextServiceTracker tlsContextServiceTracker,
						UserAuthenticatorTracker userAuthenticatorTracker,
						XmppParserServiceTracker xmppParserServiceTracker,
						RouteMessageParserServiceTracker routeMessageParserServiceTracker,
						LoggerServiceTracker loggerServiceTracker)
	{
		this.streamFeatureStracker = streamFeatureStracker;
		this.tlsContextServiceTracker = tlsContextServiceTracker;
		this.userAuthenticatorTracker = userAuthenticatorTracker;
		this.xmppParserServiceTracker = xmppParserServiceTracker;
		this.routeMessageParserServiceTracker = routeMessageParserServiceTracker;
		this.loggerServiceTracker = loggerServiceTracker;
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
	public boolean isStarted()
	{
		return started;
	}

	@Override
	public boolean isRouterConnected()
	{
		return routerConnected;
	}
	
	@Override
	public void setClientLimit(int clientLimit)
	{
		if (isStarted())
		{
			throw new IllegalStateException("c2s has started");
		}
		if (clientLimit < 0)
		{
			throw new IllegalArgumentException("clientLimit must be > 0");
		}
		this.clientLimit = clientLimit;
	}

	@Override
	public void setDomain(String domain)
	{
		if (isStarted())
		{
			throw new IllegalStateException("c2s has started");
		}
		this.domain = domain;
	}

	@Override
	public void setHostName(String hostName)
	{
		if (isStarted())
		{
			throw new IllegalStateException("c2s has started");
		}
		this.hostName = hostName;
	}

	@Override
	public void setName(String name)
	{
		if (isStarted())
		{
			throw new IllegalStateException("c2s has started");
		}
		this.name = name;
	}

	@Override
	public void setRouterIp(String routerIp)
	{
		if (isStarted())
		{
			throw new IllegalStateException("c2s has started");
		}
		this.routerIp = routerIp;
	}

	@Override
	public void setRouterPassword(String routerPassword)
	{
		if (isStarted())
		{
			throw new IllegalStateException("c2s has started");
		}
		this.routerPassword = routerPassword;
	}

	@Override
	public void setRouterPort(int routerPort)
	{
		if (isStarted())
		{
			throw new IllegalStateException("c2s has started");
		}
		this.routerPort = routerPort;
	}

	/**
	 * @return the xmppClientPort
	 */
	public int getXmppClientPort()
	{
		return xmppClientPort;
	}

	/**
	 * @param xmppClientPort the xmppClientPort to set
	 */
	public void setXmppClientPort(int xmppClientPort)
	{
		if (isStarted())
		{
			throw new IllegalStateException("c2s has started");
		}
		this.xmppClientPort = xmppClientPort;
	}

	@Override
	public void start()
	{
		if (isStarted())
		{
			throw new IllegalStateException("c2s has started");
		}
		
		loggerServiceTracker.info("c2s starting...");
		
		if (getName() == null || getName().isEmpty())
		{
			loggerServiceTracker.error("name has not been set");
			throw new IllegalStateException("name has not been set");
		}
		
		if (getDomain() == null || getDomain().isEmpty())
		{
			loggerServiceTracker.error("domain has not been set");
			throw new IllegalStateException("domain has not been set");
		}
		
		if (getRouterIp() == null || getRouterIp().isEmpty())
		{
			loggerServiceTracker.error("routerIp has not been set");
			throw new IllegalStateException("routerIp has not been set");
		}
		
		if (getRouterPassword() == null || getRouterPassword().isEmpty())
		{
			loggerServiceTracker.error("routerPassword has not been set");
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
			loggerServiceTracker.error("c2s starting failure:" + e.getMessage());
			exit();
			return;
		}
		
		started = true;
		loggerServiceTracker.info("c2s started");
	}

	private void startService() throws IOException
	{
		loggerServiceTracker.info("starting c2sAcceptor");
		
		c2sAcceptor = new SocketAcceptor();
		IoAcceptorConfig config = new SocketAcceptorConfig();
		config.setThreadModel(ThreadModel.MANUAL);
		DefaultIoFilterChainBuilder chain = config.getFilterChain();

		chain.addFirst("xmppCodec", new ProtocolCodecFilter(new XmppCodecFactory()));
		
		int xmppClientPort = getXmppClientPort();
		
		c2sAcceptor.bind(new InetSocketAddress(xmppClientPort), new C2sHandler(), config);
		
		loggerServiceTracker.info("c2sAcceptor start successful");
	}

	private void connect2Router() throws Exception
	{
		loggerServiceTracker.info("connecting to router");
		
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
		
		loggerServiceTracker.info("connecting to router successful");
	}

	@Override
	public void stop()
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
		
		if (c2sAcceptor != null)
		{
			c2sAcceptor.unbindAll();
			c2sAcceptor = null;
		}
		clientSessions.clear();
		started = false;
	}

	void addClientSession(ClientSessionImpl clientSession) throws OpenStreamException
	{
		if (clientSessions.containsKey(clientSession.getStreamId()))
		{
			throw new OpenStreamException("stream duplication");
		}
		clientSessions.put(clientSession.getStreamId(), clientSession);
	}
	
	void removeClientSession(ClientSessionImpl clientSession)
	{
		clientSessions.remove(clientSession.getStreamId());
	}
	

	@Override
	public int getSessionCount()
	{
		return clientSessions.size();
	}
	
	private class RouterHandler implements IoHandler
	{
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception
		{
			loggerServiceTracker.debug("session" + session + ": exceptionCaught:" + cause.getMessage());
			cause.printStackTrace();
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception
		{
			loggerServiceTracker.debug("session" + session + ": messageReceived:\n" + message);
			
			String xml = message.toString();
			if (xml.equals("</stream:stream>"))
			{
				session.close();
				return;
			}
			
			StringReader strReader = new StringReader(xml);
			XmlPullParser parser = new MXParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
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
				loggerServiceTracker.info("router auth successful");
			}
			else if ("failed".equals(elementName))
			{
				loggerServiceTracker.error("password error");
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
				ClientSessionImpl clientSession = clientSessions.get(streamId);
				if (clientSession != null)
				{
					if (routeMessage.isCloseStream())
					{
						clientSession.write(CloseStream.getCloseStream());
						clientSession.setProperty("sessionCleared");
						clientSession.close();
					}
					else
					{
						clientSession.write(routeMessage.getXmlStanza());
					}
					
				}
			}
		}

		private void handleStream(XmlPullParser parser, IoSession session)
		{
			String xmlns = parser.getNamespace(null);
			String from = parser.getAttributeValue("", "from");
			String id = parser.getAttributeValue("", "id");
			
			if (!C2SROUTER_NAMESPACE.equals(xmlns))
			{
				loggerServiceTracker.error("namespace error:" + xmlns);
				session.close();
				return;
			}
			if (!"router".equals(from))
			{
				loggerServiceTracker.error("from error:" + from);
				session.close();
				return;
			}
			session.setAttribute("streamId", id);
			
			loggerServiceTracker.debug("open stream successful");
			
			session.write("<internal xmlns='" + C2SROUTER_AUTH_NAMESPACE + "'" +
						" c2sname='" + getName() + "' password='" + getRouterPassword() + "'/>");
		}

		@Override
		public void messageSent(IoSession session, Object message) throws Exception
		{
			if (loggerServiceTracker.isDebugEnabled())
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
				loggerServiceTracker.debug("session" + session + ": messageSent:\n" + s);
			}
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception
		{
			loggerServiceTracker.debug("session" + session + ": sessionClosed");
			exit();
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception
		{
			loggerServiceTracker.debug("session" + session + ": sessionCreated");
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception
		{
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception
		{
			loggerServiceTracker.debug("session" + session + ": sessionOpened");
			
			session.write("<stream:stream xmlns='" + C2SROUTER_NAMESPACE + "'" +
						" xmlns:stream='http://etherx.jabber.org/streams'" +
						" to='router'" +
						" domain='" + getDomain() + "'>");
		}
		
	}
	
	private class C2sHandler implements IoHandler
	{
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception
		{
			loggerServiceTracker.error("session" + session + ": messageReceived:\n" + cause.getMessage());
			
			// TODO remove it in release
			cause.printStackTrace();
			
			if (cause instanceof OpenStreamException)
			{
				session.write(new StreamError(StreamError.Condition.conflict));
				session.write(CloseStream.getCloseStream());
				session.close();
			}
			
			
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception
		{
			loggerServiceTracker.debug("session" + session + ": messageReceived:\n" + message);
			String xml = message.toString();
			if (xml.startsWith("<?xml"))
			{
				return;
			}
			
			if ("</stream:stream>".equals(xml))
			{
				ClientSessionImpl clientSession = (ClientSessionImpl) session.getAttachment();
				if (clientSession == null)
				{
					session.close();
				}
				else
				{
					clientSession.close();
				}
				return;
			}
			
			
			XmlStanza stanza = xmppParserServiceTracker.getParser().parseXml(xml);

			if (stanza instanceof Stream)
			{
				ClientSessionImpl clientSession = (ClientSessionImpl) session.getAttachment();
				if (clientSession == null)
				{
					handleOpenStream((Stream) stanza, session, ChristyStreamFeature.SupportedType.afterConnected);
				}
				else
				{
					if (clientSession.isUsingTLS())
					{
						handleOpenStream((Stream) stanza, session, ChristyStreamFeature.SupportedType.afterTLS);
					}
					else if (clientSession.getStatus() == ClientSessionImpl.Status.authenticated)
					{
						handleOpenStream((Stream) stanza, session, ChristyStreamFeature.SupportedType.afterAuth);
					}
				}
			}
			else if (stanza instanceof StartTls)
			{
				handleStarttls((StartTls) stanza, session);
			}
			else if (stanza instanceof Auth)
			{
				handleAuth((Auth) stanza, session);
			}
			else if (stanza instanceof Challenge)
			{
				handleChallenge((Challenge) stanza, session);
			}
			else if (stanza instanceof Iq)
			{
				handleIq((Iq) stanza, session);
			}
			else
			{
				handleStanza(stanza, session);
			}
		}

		private void handleStanza(XmlStanza stanza, IoSession session)
		{
			// TODO check case sentity
			if (stanza instanceof Packet)
			{
				Packet packet = (Packet) stanza;
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
			}
			
			ClientSession clientSession = (ClientSession) session.getAttachment();
			
			RouteMessage routeMessage = new RouteMessage(getName(), clientSession.getStreamId());
			routeMessage.setToUserNode(clientSession.getUsername());
			routeMessage.setXmlStanza(stanza);

			routerSession.write(routeMessage.toXml());
		}

		private void handleIq(Iq iq, IoSession session)
		{
			if (tryHandling(iq, session))
			{
				return;
			}
			
			handleStanza(iq, session);
		}

		private boolean tryHandling(Iq iq, IoSession session)
		{

			if (iq.getExtension("ping", "urn:xmpp:ping") != null)
			{
				JID jid = iq.getTo();
				if (jid != null && !jid.toPrepedBareJID().equals(getDomain()))
				{
					// ping other user
					return false;
				}
				
				if (iq.getType() != Iq.Type.get)
				{
					StreamError error = new StreamError(StreamError.Condition.undefined_condition);
					session.write(error);
					session.write(CloseStream.getCloseStream());
					session.close();
					return true;
				}
				
				Iq iqResult = new Iq(Iq.Type.result);
				iqResult.setFrom(new JID(getDomain()));
				iqResult.setStanzaId(iq.getStanzaId());

				
				session.write(iqResult);
				return true;
				
			}
			
			
			return false;
		}

		private void handleChallenge(Challenge challenge, IoSession session)
		{
			String content = challenge.getContent();
			
			try
			{
				ClientSession clientSession = (ClientSession) session.getAttachment();
				userAuthenticatorTracker.response(clientSession, content);
				if (clientSession.getStatus() == ClientSession.Status.authenticated)
				{
					Success success = new Success();
					clientSession.write(success);
				}
			}
			catch (UnauthorizedException e)
			{
				e.printStackTrace();
				
				Failure failure = new Failure();
				failure.setNamespace("urn:ietf:params:xml:ns:xmpp-sasl");
				session.write(failure);
				session.write(CloseStream.getCloseStream());
				session.close();
				return;
			}
		}

		private void handleAuth(Auth auth, IoSession session)
		{
			
			String mechanism = auth.getMechanism();
			String content = auth.getContent();
			ClientSessionImpl clientSession = (ClientSessionImpl) session.getAttachment();
			
			try
			{
				userAuthenticatorTracker.authenticate(clientSession, content, mechanism);
				clientSession.setStatus(ClientSession.Status.authenticated);
				
				Success success = new Success();
				clientSession.write(success);
			}
			catch (UnauthorizedException e)
			{
				e.printStackTrace();
				
				Failure failure = new Failure();
				failure.setNamespace(Failure.SASL_FAILURE_NS);
				session.write(failure);
				session.write(CloseStream.getCloseStream());
				session.close();
				
				return;
			}
			catch (UnsupportedMechanismException e)
			{
				e.printStackTrace();
				
				Failure failure = new Failure();
				failure.setError(Failure.Error.invalid_mechanism);
				failure.setNamespace(Failure.SASL_FAILURE_NS);
				session.write(failure);
				session.write(CloseStream.getCloseStream());
				session.close();
				
				return;
			}
		}

		private void handleStarttls(StartTls startTls, IoSession session)
		{

			ClientSessionImpl clientSession = (ClientSessionImpl) session.getAttachment();
			ClientSessionImpl.Status status = clientSession.getStatus();

			if (status != ClientSessionImpl.Status.connected || clientSession.isUsingTLS())
			{
				Failure failure = new Failure("urn:ietf:params:xml:ns:xmpp-tls");
				session.write(failure);
				session.close();
				return;
			}

			SSLContext context = tlsContextServiceTracker.getTlsContext();
			SSLFilter sslFilter = new SSLFilter(context);
			sslFilter.setUseClientMode(false);

			session.getFilterChain().addFirst("tlsFilter", sslFilter);

			// Disable encryption temporarilly.
			// This attribute will be removed by SSLFilter
			// inside the Session.write() call below.
			session.setAttribute(SSLFilter.DISABLE_ENCRYPTION_ONCE, Boolean.TRUE);

			clientSession.setUsingTLS(true);
			Proceed proceed = new Proceed();
			session.write(proceed);

		}

		private void handleOpenStream(Stream stream, IoSession session, ChristyStreamFeature.SupportedType supportedType) throws OpenStreamException
		{
			
			ClientSessionImpl clientSession = (ClientSessionImpl) session.getAttachment();
			if (clientSession != null)
			{
				clientSessions.remove(clientSession.getStreamId());
				session.setAttachment(null);
			}
			
			String streamId = nextStreamId();
			clientSession = new ClientSessionImpl(session, streamId, C2SManagerImpl.this);
			session.setAttachment(clientSession);
			
			Stream responseStream = new Stream();
			responseStream.setFrom(new JID(getDomain()));
			responseStream.setStanzaId(streamId);
			
			clientSession.write(responseStream);
			
			if (supportedType == ChristyStreamFeature.SupportedType.afterConnected)
			{
				clientSession.setStatus(ClientSessionImpl.Status.connected);
			}
			
			sendFeature(clientSession, supportedType);
		}

		private void sendFeature(ClientSessionImpl clientSession, SupportedType type)
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
			
			
			clientSession.write(streamFeature);
		}

		@Override
		public void messageSent(IoSession session, Object message) throws Exception
		{
			String messageStr = "";
			if (message instanceof String)
			{
				messageStr = message.toString();
			}
			else if (message instanceof XmlStanza)
			{
				messageStr = ((XmlStanza) message).toXml();
			}
			loggerServiceTracker.debug("session" + session + ": messageSent:\n" + messageStr);
			
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception
		{
			ClientSessionImpl clientSession = (ClientSessionImpl) session.getAttachment();
			
			if (!clientSession.containsProperty("sessionCleared"))
			{
				RouteMessage routeMessage = new RouteMessage(getName(), clientSession.getStreamId());
				routeMessage.setToUserNode(clientSession.getUsername());
				routeMessage.setCloseStream(true);
				routerSession.write(routeMessage.toXml());
			}
			

			if (clientSession != null)
			{
				clientSession.close();
			}
			loggerServiceTracker.debug("session" + session + ": sessionClosed:\n");
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception
		{
			// TODO Auto-generated method stub
			
		}
	}

}
