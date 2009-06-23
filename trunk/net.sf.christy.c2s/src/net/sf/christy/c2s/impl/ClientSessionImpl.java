/**
 * 
 */
package net.sf.christy.c2s.impl;

import java.net.SocketAddress;
import java.util.Map;

import org.apache.mina.common.IoSession;

import net.sf.christy.c2s.ClientSession;
import net.sf.christy.util.AbstractPropertied;

/**
 * @author noah
 *
 */
public class ClientSessionImpl extends AbstractPropertied implements ClientSession
{
	private IoSession iosession;
	
	private String streamId;
	
	private boolean authenticated;
	
	private boolean connected;
	
	private boolean resourceBinded;
	
	private boolean sessionBinded;
	
	private boolean usingCompression;
	
	private boolean usingTLS;
	
	private Map<String, ClientSessionImpl> clientSessions;
	
	/**
	 * 
	 * @param iosession
	 * @param streamId
	 * @param clientSessions
	 */
	public ClientSessionImpl(IoSession iosession, String streamId, Map<String, ClientSessionImpl> clientSessions)
	{
		this.iosession = iosession;
		this.streamId = streamId;
		this.clientSessions = clientSessions;
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
		clientSessions.remove(getStreamId());
		streamId = null;
		authenticated = false;
		connected = false;
		resourceBinded = false;
		sessionBinded = false;
		usingCompression = false;
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
	 * @see net.sf.christy.c2s.ClientSession#isAuthenticated()
	 */
	@Override
	public boolean isAuthenticated()
	{
		return authenticated;
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.c2s.ClientSession#isConnected()
	 */
	@Override
	public boolean isConnected()
	{
		return connected;
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.c2s.ClientSession#isResourceBinded()
	 */
	@Override
	public boolean isResourceBinded()
	{
		return resourceBinded;
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.c2s.ClientSession#isSessionBinded()
	 */
	@Override
	public boolean isSessionBinded()
	{
		return sessionBinded;
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

}
