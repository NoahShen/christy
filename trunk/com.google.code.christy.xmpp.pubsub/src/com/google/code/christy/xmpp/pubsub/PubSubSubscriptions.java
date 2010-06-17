package com.google.code.christy.xmpp.pubsub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.code.christy.xmpp.XmlStanza;

public class PubSubSubscriptions implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7617271835017101678L;

	private String node;
	
	private List<PubSubSubscriptionItem> subs = new ArrayList<PubSubSubscriptionItem>();
	
	public String getNode()
	{
		return node;
	}

	public void setNode(String node)
	{
		this.node = node;
	}

	public void addPubSubSubscription(PubSubSubscriptionItem sub)
	{
		subs.add(sub);
	}
	
	public void removePubSubSubscription(PubSubSubscriptionItem sub)
	{
		subs.remove(sub);
	}
	
	public Collection<PubSubSubscriptionItem> getSubscriptions()
	{
		return subs;
	}
	
	@Override
	public String toXml()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<subscriptions");
		
		if (getNode() != null)
		{
			buf.append(" node=\"").append(getNode()).append("\"");
		}
		
		if (subs.isEmpty())
		{
			buf.append("/>");
		}
		else
		{
			buf.append(">");
			for (PubSubSubscriptionItem sub : subs)
			{
				buf.append(sub.toXml());
			}
			buf.append("</subscriptions>");
		}
		
		
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PubSubSubscriptions pubSubSubscriptions = (PubSubSubscriptions) super.clone();
		pubSubSubscriptions.node = this.node;
		pubSubSubscriptions.subs = new ArrayList<PubSubSubscriptionItem>();
		for (PubSubSubscriptionItem sub : subs)
		{
			pubSubSubscriptions.subs.add((PubSubSubscriptionItem) sub.clone());
		}
		return pubSubSubscriptions;
	}
	

}
