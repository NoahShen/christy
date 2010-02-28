/**
 * 
 */
package com.google.code.christy.c2s.defaultc2s;

import java.net.SocketAddress;

import org.apache.mina.common.IoSession;

import com.google.code.christy.c2s.ClientSession;
import com.google.code.christy.c2s.OpenStreamException;
import com.google.code.christy.util.AbstractPropertied;
import com.google.code.christy.xmpp.XmlStanza;


/**
 * @author noah
 *
 */
public class ClientSessionImpl extends AbstractPropertied implements ClientSession
{
	private String username;
	
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
	 * @throws OpenStreamException 
	 */
	public ClientSessionImpl(IoSession iosession, String streamId, C2SManagerImpl c2sManager) throws OpenStreamException
	{
		this.iosession = iosession;
		
		String username = (String) iosession.getAttribute("username");
		if (username != null)
		{
			// TODO check case sentity
			this.username = username.toLowerCase();
		}
		
		this.streamId = streamId;
		this.c2sManager = c2sManager;
		c2sManager.addClientSession(this);
	}

	
	/**
	 * @return the username
	 */
	public String getUsername()
	{
		if (username == null)
		{
			return (String) iosession.getAttribute("username");
		}
		return username;
	}


	/**
	 * @param username the username to set
	 */
	public void setUsername(String username)
	{
		// TODO check case sentity
		username = username.toLowerCase();
		
		this.username = username;
		iosession.setAttribute("username", username);
	}


	@Override
	public synchronized void close()
	{
		if (status == Status.disconnected)
		{
			return;
		}
		c2sManager.removeClientSession(this);
		status = Status.disconnected;
		usingTLS = false;
		
		if (iosession != null && !iosession.isConnected())
		{
			iosession.close();
		}

	}

	@Override
	public SocketAddress getClientAddress()
	{
		if (iosession != null)
		{
			return iosession.getRemoteAddress();
		}
		return null;
	}

	@Override
	public String getStreamId()
	{
		return streamId;
	}

	@Override
	public boolean isUsingCompression()
	{
		return usingCompression;
	}

	@Override
	public boolean isUsingTLS()
	{
		return usingTLS;
	}

	@Override
	public void write(String stanza)
	{
		iosession.write(stanza);
	}

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
