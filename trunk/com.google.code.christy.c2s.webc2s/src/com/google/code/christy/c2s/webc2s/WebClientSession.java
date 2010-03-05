package com.google.code.christy.c2s.webc2s;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpServletResponse;

import com.google.code.christy.c2s.ClientSession;
import com.google.code.christy.util.AbstractPropertied;
import com.google.code.christy.xmpp.XmlStanza;

public class WebClientSession extends AbstractPropertied implements ClientSession
{
	private String username;
	
	private String streamId;
	
	private Status status = Status.disconnected;
	
	private WebC2SManager c2sManager;
	
	private long lastActive;
	
	private int wait;
	
	private String lastKey;
	
	private boolean ack;
	
	private ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<String>();
	
	/**
	 * @param streamId
	 * @param c2sManager
	 */
	public WebClientSession(String streamId, WebC2SManager c2sManager, int wait)
	{
		this.streamId = streamId;
		this.c2sManager = c2sManager;
		this.wait = wait;
		this.lastActive = System.currentTimeMillis();
		c2sManager.addWebClientSession(this);
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status)
	{
		this.status = status;
	}

	@Override
	public void close()
	{
		c2sManager.removeWebClientSession(this);
	}

	@Override
	public Status getStatus()
	{
		return status;
	}

	@Override
	public String getStreamId()
	{
		return streamId;
	}

	@Override
	public String getUsername()
	{
		return username;
	}
	
	@Override
	public void setUsername(String username)
	{
		this.username = username;
	}

	@Override
	public void write(String stanza)
	{
		messageQueue.add(stanza);
	}

	@Override
	public void write(XmlStanza stanza)
	{
		messageQueue.add(stanza.toXml());
	}

	public String[] getAllMessage()
	{
		return messageQueue.toArray(new String[]{});
	}
	
	public boolean hasMessage()
	{
		return messageQueue.isEmpty();
	}


	public void write(XmlStanza stanza, HttpServletResponse response, String rid) throws IOException
	{
		Body body = new Body();
		body.setProperty("inactivity", c2sManager.getInactivity());
		body.setProperty("requests", "2");
		body.setProperty("sid", getStreamId());
		body.setProperty("wait", getWait());
		
		body.addStanza(stanza);
		
		if (isAck() && !body.containsProperty("ack"))
		{
			body.setProperty("ack", rid);
		}
		
		response.getWriter().write(body.toXml());
	}

	public void write(Body body, HttpServletResponse response, String rid) throws IOException
	{
		if (isAck() && !body.containsProperty("ack"))
		{
			body.setProperty("ack", rid);
		}
		response.getWriter().write(body.toXml());
	}
	
	public String getLastKey()
	{
		return lastKey;
	}

	public void setLastKey(String lastKey)
	{
		this.lastKey = lastKey;
	}

	public int getWait()
	{
		return wait;
	}

	public long getLastActive()
	{
		return lastActive;
	}

	public void setLastActive(long lastActive)
	{
		this.lastActive = lastActive;
	}

	public boolean isAck()
	{
		return ack;
	}

	public void setAck(boolean ack)
	{
		this.ack = ack;
	}

}
