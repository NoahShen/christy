package com.google.code.christy.c2s.webc2s;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.continuation.Continuation;

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
	
	private int wait = 60;
	
	private String lastKey;
	
	private boolean ack;
	
	private int holded;
	
	private Continuation continuation;
	
	private ConcurrentLinkedQueue<XmlStanza> messageQueue = new ConcurrentLinkedQueue<XmlStanza>();
	
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
		status = Status.disconnected;
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
		// TODO check case sentity
		username = username.toLowerCase();
		this.username = username;
	}

	@Override
	public void write(XmlStanza stanza)
	{
		messageQueue.add(stanza);
	}

	public  XmlStanza[] getAllMessage()
	{
		XmlStanza[] stanzas = messageQueue.toArray(new XmlStanza[]{});
		messageQueue.clear();
		return stanzas;
	}
	
	public boolean hasMessage()
	{
		return !messageQueue.isEmpty();
	}


	public void write(XmlStanza stanza, HttpServletResponse response, String rid) throws IOException
	{
		Body body = new Body();
		body.setProperty("sid", getStreamId());
		
		body.addStanza(stanza);
		
		write(body, response, rid);
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

	public Continuation getContinuation()
	{
		return continuation;
	}

	public void setContinuation(Continuation continuation)
	{
		this.continuation = continuation;
	}
	
	public boolean isSuspended()
	{
		if (continuation != null)
		{
			return continuation.isSuspended();
		}
		return false;
	}

	/**
	 * @return the holded
	 */
	public int getHolded()
	{
		return holded;
	}

	/**
	 * @param holded the holded to set
	 */
	public void setHolded(int holded)
	{
		this.holded = holded;
	}

	public void increaseHolded()
	{
		++this.holded;
	}
	
	public void decreaseHolded()
	{
		--this.holded;
	}
}
