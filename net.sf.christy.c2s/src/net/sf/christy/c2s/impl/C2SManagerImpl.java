package net.sf.christy.c2s.impl;

import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import net.sf.christy.c2s.C2SManager;
import net.sf.christy.mina.XMPPCodecFactory;
import net.sf.christy.util.AbstractPropertied;
import net.sf.christy.xmpp.XMLStanza;

public class C2SManagerImpl extends AbstractPropertied implements C2SManager
{
	
	public static final String C2SROUTER_NAMESPACE = "christy:internal:c2s2router";
	
	public static final String C2SROUTER_AUTH_NAMESPACE = "christy:internal:c2s2router:auth";
	
	private final Logger logger = LoggerFactory.getLogger(C2SManagerImpl.class);
	
	private int clientLimit = 0;
	
	private String domain;
	
	private String hostName;
	
	private String name;
	
	private String routerIp;
	
	private String routerPassword;
	
	private int routerPort = 8787;
	
	private boolean started = false;
	
	private SocketConnector routerConnector;
	
	private IoSession routerSession;
	
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
	public void start()
	{
		
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
		
		
		SocketConnectorConfig socketConnectorConfig = new SocketConnectorConfig();
		socketConnectorConfig.getFilterChain().addLast("xmppCodec", new ProtocolCodecFilter(new XMPPCodecFactory()));
		socketConnectorConfig.setThreadModel(ThreadModel.MANUAL);
		
		routerConnector = new SocketConnector(Runtime.getRuntime().availableProcessors() + 1, Executors.newCachedThreadPool());
		InetSocketAddress address =new InetSocketAddress(getRouterIp(), getRouterPort());
		
		routerConnector.connect(address, new RouterHandler(), socketConnectorConfig);
		
		logger.info("c2s successful start");
	}

	@Override
	public void stop()
	{
		// TODO Auto-generated method stub

	}

	private class RouterHandler implements IoHandler
	{

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception
		{
			logger.debug("session" + session + ": exceptionCaught:" + cause.getMessage());
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
				logger.debug("pare xml error:[session" + session + "]:" + e.getMessage());
				return;
			}

			String elementName = parser.getName();
			if ("stream".equals(elementName) || "stream:stream".equals(elementName))
			{
				handleStream(parser, session);
			}
			else if ("success".equals(elementName))
			{
				routerSession = session;
				logger.info("connect to router successful");
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
}
