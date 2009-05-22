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

import net.sf.christy.router.RouterManager;
import net.sf.christy.util.AbstractPropertied;
import net.sf.christy.xmpp.CloseStream;
import net.sf.christy.xmpp.StreamError;
import net.sf.christy.xmpp.XMLStanza;

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
	
	private final Logger logger = LoggerFactory.getLogger(RouterManagerImpl.class);

	private IoAcceptor c2sAcceptor;
	
	private boolean started = false;
	
	private int c2sPort = 5333;
	
	private Map<String, C2sSessionImpl> c2sSessions = new ConcurrentHashMap<String, C2sSessionImpl>();
	
	private Map<String, String> c2sList = new ConcurrentHashMap<String, String>();
	
	@Override
	public void addC2s(String name, String md5Password)
	{
		c2sList.put(name, md5Password);
	}

	@Override
	public void addSm(String name, String md5Password)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addModule(String subDomain, String md5Password)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeModule(String subDomain)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getC2sLimit()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDomain()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSmLimit()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeC2s(String name)
	{
		c2sList.remove(name);
	}

	@Override
	public void removeSm(String name)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setC2sLimit(int limit)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setDomain(String domain)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setName(String name)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setSmLimit(int smLimit)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void start()
	{
		logger.info("starting...");
		
		c2sAcceptor = new SocketAcceptor();
		IoAcceptorConfig config = new SocketAcceptorConfig();
		DefaultIoFilterChainBuilder chain = config.getFilterChain();

		chain.addFirst("xmppCodec", new ProtocolCodecFilter(new XMPPCodecFactory()));
		
		int c2sPort = getC2sPort();
		
		try
		{
			c2sAcceptor.bind(new InetSocketAddress(c2sPort), new C2sHandler(), config);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			logger.error("start failure:" + e.getMessage());
		}
		
		started = true;
		logger.info("successful start");
	}

	@Override
	public void stop()
	{
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setC2sPort(int c2sPort)
	{
		this.c2sPort = c2sPort;
	}

	@Override
	public void setSmPort(int smPort)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getS2sPort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setS2sPort(int port)
	{
		// TODO Auto-generated method stub
		
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
	
	private class C2sHandler implements IoHandler
	{

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception
		{
			// TODO Auto-generated method stub
			logger.debug("session" + session + ": exceptionCaught:" + cause.getMessage());
			cause.printStackTrace();
			
			
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception
		{
			// TODO Auto-generated method stub
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
				C2sSessionImpl c2sSession = (C2sSessionImpl) session.getAttachment();
				if (c2sSession == null)
				{
					handleStream(parser, session);
				}
			}
			else if ("internal".equals(elementName))
			{
				handleInternal(parser, session);
			}
		}

		private void handleInternal(XmlPullParser parser, IoSession session)
		{
			String streamId = session.getAttribute("streamId").toString();
			if (streamId != null)
			{
				String c2sname = parser.getAttributeValue("", "c2sname");
				String password = parser.getAttributeValue("", "password");
				
				if (!c2sList.containsKey(c2sname))
				{
					StreamError error = new StreamError();
					error.addApplicationCondition("unregistered", C2SROUTER_AUTH_NAMESPACE);
					session.write(error);
					session.write(new CloseStream());
					session.close();
					return;
				}
				
				String registeredPwd = c2sList.get(c2sname);
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
					session.write(new CloseStream());
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

		private void handleStream(XmlPullParser parser, IoSession session)
		{
			String xmlns = parser.getAttributeValue("", "xmlns");
			String to = parser.getAttributeValue("", "to");
			
			if (!xmlns.equals(C2SROUTER_NAMESPACE))
			{
				StreamError error = new StreamError(StreamError.Condition.invalid_namespace);
				session.write(error);
				session.write(new CloseStream());
				session.close();
				return;
			}
			
			if (!to.equals("router"))
			{
				StreamError error = new StreamError(StreamError.Condition.host_gone);
				session.write(error);
				session.write(new CloseStream());
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
			// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception
		{
			logger.debug("session" + session + ": sessionOpened");
		}
		
	}
}
