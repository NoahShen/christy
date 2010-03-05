package com.google.code.christy.c2s.webc2s;

import java.util.concurrent.ConcurrentLinkedQueue;

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
	
	private ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<String>();
	/**
	 * @param streamId
	 * @param c2sManager
	 */
	public WebClientSession(String streamId, WebC2SManager c2sManager)
	{
		this.streamId = streamId;
		this.c2sManager = c2sManager;
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
		// TODO Auto-generated method stub

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

}
