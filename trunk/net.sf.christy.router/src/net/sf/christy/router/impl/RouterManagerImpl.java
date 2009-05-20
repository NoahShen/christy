package net.sf.christy.router.impl;

import java.io.IOException;
import java.net.InetSocketAddress;

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

import net.sf.christy.router.RouterManager;
import net.sf.christy.util.AbstractPropertied;

/**
 * 
 * @author noah
 *
 */
public class RouterManagerImpl extends AbstractPropertied implements RouterManager
{
	final Logger logger = LoggerFactory.getLogger(RouterManagerImpl.class);

	private IoAcceptor c2sAcceptor;
	
	@Override
	public void addC2s(String name, String md5Password)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addSm(String name, String md5Password)
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
		// TODO Auto-generated method stub

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void stop()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getC2sPort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSmPort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setC2sPort(int port)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSmPort(int smPort)
	{
		// TODO Auto-generated method stub
		
	}

	private class C2sHandler implements IoHandler
	{

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception
		{
			// TODO Auto-generated method stub
			System.out.println("session:" + session + "exceptionCaught" + "\n");
			cause.printStackTrace();
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception
		{
			// TODO Auto-generated method stub
			System.out.println("session:" + session + "messageReceived:" + "\n");
			System.out.println(message);
		}

		@Override
		public void messageSent(IoSession session, Object message) throws Exception
		{
			// TODO Auto-generated method stub
			System.out.println("session:" + session + "messageSent:" + "\n");
			System.out.println(message);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception
		{
			// TODO Auto-generated method stub
			System.out.println("session:" + session + "sessionClosed" + "\n");
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception
		{
			// TODO Auto-generated method stub
			System.out.println("session:" + session + "sessionCreated" + "\n");
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
			System.out.println("session:" + session + "sessionOpened" + "\n");
		}
		
	}
}
