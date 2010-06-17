package com.google.code.christy.xmpp.pubsub;

import com.google.code.christy.xmpp.XmlStanza;

public class PubSubSubscriptionItem implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -852218418997032739L;

	private String node;
	
	private String jid;
	
	private SubscriptionType subscriptionType;
	
	private String subId;
	
	public PubSubSubscriptionItem(String node, String jid, SubscriptionType subscriptionType)
	{
		super();
		this.node = node;
		this.jid = jid;
		this.subscriptionType = subscriptionType;
	}

	public String getNode()
	{
		return node;
	}

	public void setNode(String node)
	{
		this.node = node;
	}

	public String getJid()
	{
		return jid;
	}

	public void setJid(String jid)
	{
		this.jid = jid;
	}

	public SubscriptionType getSubscriptionType()
	{
		return subscriptionType;
	}

	public void setSubscriptionType(SubscriptionType subscriptionType)
	{
		this.subscriptionType = subscriptionType;
	}

	public String getSubId()
	{
		return subId;
	}

	public void setSubId(String subId)
	{
		this.subId = subId;
	}

	@Override
	public String toXml()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<subscription");
		if (getNode() != null)
		{
			buf.append(" node=\"").append(getNode()).append("\"");
		}
		
		if (getJid() != null)
		{
			buf.append(" jid=\"").append(getJid()).append("\"");
		}
		
		if (getSubscriptionType() != null)
		{
			buf.append(" subscription=\"").append(getSubscriptionType().name()).append("\"");
		}
		
		if (getSubId() != null)
		{
			buf.append(" subid=\"").append(getSubId()).append("\"");
		}
		
		buf.append("/>");
		
		
		return buf.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PubSubSubscriptionItem subscription = (PubSubSubscriptionItem) super.clone();
		subscription.node = this.node;
		subscription.jid = this.jid;
		subscription.subId = this.subId;
		subscription.subscriptionType = this.subscriptionType;
		return subscription;
	}
	

	public enum SubscriptionType
	{
		subscribed,
		
		unconfigured,
		
		none
	}
}
