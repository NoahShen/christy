package net.sf.christy.c2s.impl;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import net.sf.christy.c2s.C2SManager;
import net.sf.christy.mina.XMPPCodecFactory;
import net.sf.christy.util.AbstractPropertied;
import net.sf.christy.util.StringUtils;
import net.sf.christy.xmpp.CloseStream;
import net.sf.christy.xmpp.JID;
import net.sf.christy.xmpp.Stream;
import net.sf.christy.xmpp.StreamError;
import net.sf.christy.xmpp.XMLStanza;

public class C2SManagerImpl extends AbstractPropertied implements C2SManager
{

	private static String prefix = StringUtils.randomString(10) + "-";

	private static long id = 0;

	public static synchronized String nextStreamId()
	{
		return prefix + Long.toString(id++);
	}
	
	public static final String C2SROUTER_NAMESPACE = "christy:internal:c2s2router";
	
	public static final String C2SROUTER_AUTH_NAMESPACE = "christy:internal:c2s2router:auth";
	
	private final Logger logger = LoggerFactory.getLogger(C2SManagerImpl.class);
	
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
		
		logger.info("c2s starting...");
		
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
			logger.error("c2s starting failure:" + e.getMessage());
			exit();
			return;
		}
		
		started = true;
		logger.info("c2s started");
	}

	private void startService() throws IOException
	{
		logger.info("starting c2sAcceptor");
		
		SocketConnectorConfig socketConnectorConfig = new SocketConnectorConfig();
		socketConnectorConfig.getFilterChain().addLast("xmppCodec", new ProtocolCodecFilter(new XMPPCodecFactory()));
		socketConnectorConfig.setThreadModel(ThreadModel.MANUAL);
		
		c2sAcceptor = new SocketAcceptor();
		IoAcceptorConfig config = new SocketAcceptorConfig();
		DefaultIoFilterChainBuilder chain = config.getFilterChain();

		chain.addFirst("xmppCodec", new ProtocolCodecFilter(new XMPPCodecFactory()));
		
		int xmppClientPort = getXmppClientPort();
		
		c2sAcceptor.bind(new InetSocketAddress(xmppClientPort), new C2sHandler(), config);
		
		logger.info("c2sAcceptor start successful");
	}

	private void connect2Router() throws Exception
	{
		logger.info("connecting to router");
		
		SocketConnectorConfig socketConnectorConfig = new SocketConnectorConfig();
		socketConnectorConfig.setConnectTimeout(30);
		socketConnectorConfig.getFilterChain().addLast("xmppCodec", new ProtocolCodecFilter(new XMPPCodecFactory()));
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
		started = false;
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
			}
			
			StringReader strReader = new StringReader(xml);
			XmlPullParser parser = new MXParser();
			parser.setInput(strReader);

			try
			{
				parser.next();
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
				else if (message instanceof XMLStanza)
				{
					s = ((XMLStanza)message).toXML();
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
			logger.debug("session" + session + ": sessionOpened");
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
	
	private class C2sHandler implements IoHandler
	{
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception
		{
			// TODO Auto-generated method stub
			logger.debug("session" + session + ": messageReceived:\n" + message);
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
			
			StringReader strReader = new StringReader(xml);
			XmlPullParser parser = new MXParser();
			parser.setInput(strReader);

			try
			{
				parser.next();
			}
			catch (Exception e)
			{
				// e.printStackTrace();
				throw e;
			}

			String elementName = parser.getName();
			if ("stream".equals(elementName) || "stream:stream".equals(elementName))
			{
				ClientSessionImpl clientSession = (ClientSessionImpl) session.getAttachment();
				if (clientSession == null)
				{
					handleOpenStream(parser, session);
				}
			}
			
		}

		private void handleOpenStream(XmlPullParser parser, IoSession session)
		{
			String xmlns = parser.getAttributeValue("", "xmlns");
			String to = parser.getAttributeValue("", "to");
			String version = parser.getAttributeValue("", "version");

			
			if (!xmlns.equals(Stream.JABBER_CLIENT_NAMESPACE))
			{
				StreamError error = new StreamError(StreamError.Condition.invalid_namespace);
				session.write(error);
				session.write(new CloseStream());
				session.close();
				return;
			}
			
			if (!getDomain().equals(to))
			{
				StreamError error = new StreamError(StreamError.Condition.host_gone);
				session.write(error);
				session.write(new CloseStream());
				session.close();
				return;
			}
			
			if (!"1.0".equals(version))
			{
				StreamError error = new StreamError(StreamError.Condition.unsupported_version);
				session.write(error);
				session.write(new CloseStream());
				session.close();
				return;
			}
			
			
			String streamId = nextStreamId();
			session.setAttribute("streamId", streamId);
			ClientSessionImpl clientSession = new ClientSessionImpl(session, streamId, clientSessions);
			
			Stream responseStream = new Stream();
			responseStream.setFrom(new JID(getDomain()));
			responseStream.setStanzaID(streamId);
			
			clientSession.write(responseStream);
			
			
		}

		@Override
		public void messageSent(IoSession session, Object message) throws Exception
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception
		{
			// TODO Auto-generated method stub
			
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
