package com.google.code.christy.xmpp.pubsub;

import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.XmlStanza;

public class PubSubSubscribe implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3618932258007554440L;

	private String node;
	
	private JID subscriberJid;
	
	
	public PubSubSubscribe(String node, JID subscriberJid)
	{
		super();
		this.node = node;
		this.subscriberJid = subscriberJid;
	}


	public String getNode()
	{
		return node;
	}


	public JID getSubscriberJid()
	{
		return subscriberJid;
	}


	@Override
	public String toXml()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<subscribe");
		
		if (getNode() != null)
		{
			buf.append(" node=\"").append(getNode()).append("\"");
		}
		
		if (getSubscriberJid() != null)
		{
			buf.append(" jid=\"").append(getSubscriberJid().toBareJID()).append("\"");
		}
		
		buf.append("/>");
		
		return buf.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PubSubSubscribe pubSubSubscribe = (PubSubSubscribe) super.clone();
		pubSubSubscribe.node = this.node;
		pubSubSubscribe.subscriberJid = this.subscriberJid;
		return pubSubSubscribe;
	}
	

}
