/**
 * 
 */
package net.sf.christy.c2s.impl;

import java.net.SocketAddress;

import org.apache.mina.common.IoSession;

import net.sf.christy.c2s.ClientSession;
import net.sf.christy.util.AbstractPropertied;
import net.sf.christy.xmpp.XmlStanza;

/**
 * @author noah
 *
 */
public class ClientSessionImpl extends AbstractPropertied implements ClientSession
{
	private IoSession iosession;
	
	private String streamId;
	
	private Status status = Status.disconnected;
	
	private boolean usingCompression;
	
	private boolean usingTLS;
	
	private C2SManagerImpl c2sManager;
	
	/**
	 * 
	 * @param iosession
	 * @param streamId
	 * @param clientSessions
	 */
	public ClientSessionImpl(IoSession iosession, String streamId, C2SManagerImpl c2sManager)
	{
		this.iosession = iosession;
		this.streamId = streamId;
		this.c2sManager = c2sManager;
		c2sManager.addClientSession(this);
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.c2s.ClientSession#close()
	 */
	@Override
	public void close()
	{
		if (iosession != null && !iosession.isConnected())
		{
			iosession.close();
		}
		c2sManager.removeClientSession(this);
		streamId = null;
		status = Status.disconnected;
		usingTLS = false;
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.c2s.ClientSession#getClientAddress()
	 */
	@Override
	public SocketAddress getClientAddress()
	{
		if (iosession != null)
		{
			return iosession.getRemoteAddress();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.c2s.ClientSession#getStreamId()
	 */
	@Override
	public String getStreamId()
	{
		return streamId;
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.c2s.ClientSession#isUsingCompression()
	 */
	@Override
	public boolean isUsingCompression()
	{
		return usingCompression;
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.c2s.ClientSession#isUsingTLS()
	 */
	@Override
	public boolean isUsingTLS()
	{
		return usingTLS;
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.c2s.ClientSession#write(java.lang.String)
	 */
	@Override
	public void write(String stanza)
	{
		iosession.write(stanza);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.christy.c2s.ClientSession#write(java.lang.String)
	 */
	@Override
	public void write(XmlStanza stanza)
	{
		iosession.write(stanza);
	}

	@Override
	public Status getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status)
	{
		this.status = status;
	}

	/**
	 * @param usingCompression the usingCompression to set
	 */
	public void setUsingCompression(boolean usingCompression)
	{
		this.usingCompression = usingCompression;
	}

	/**
	 * @param usingTLS the usingTLS to set
	 */
	public void setUsingTLS(boolean usingTLS)
	{
		this.usingTLS = usingTLS;
	}

}
