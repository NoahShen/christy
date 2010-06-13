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
	
	private List<Subscription> subs = new ArrayList<Subscription>();
	
	public String getNode()
	{
		return node;
	}

	public void setNode(String node)
	{
		this.node = node;
	}

	public void addSubscription(Subscription sub)
	{
		subs.add(sub);
	}
	
	public void removeSubscription(Subscription sub)
	{
		subs.remove(sub);
	}
	
	public Collection<Subscription> getSubscriptions()
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
			for (Subscription sub : subs)
			{
				buf.append(sub.toXml());
			}
			buf.append("<subscriptions/>");
		}
		
		
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PubSubSubscriptions pubSubSubscriptions = (PubSubSubscriptions) super.clone();
		pubSubSubscriptions.node = this.node;
		pubSubSubscriptions.subs = new ArrayList<Subscription>();
		for (Subscription sub : subs)
		{
			pubSubSubscriptions.addSubscription((Subscription) sub.clone());
		}
		return pubSubSubscriptions;
	}
	
	public static class Subscription implements XmlStanza
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -852218418997032739L;

		private String node;
		
		private String jid;
		
		private SubscriptionType subscriptionType;
		
		private String subId;
		
		public Subscription(String node, String jid, SubscriptionType subscriptionType)
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
			Subscription subscription = (Subscription) super.clone();
			subscription.node = this.node;
			subscription.jid = this.jid;
			subscription.subId = this.subId;
			subscription.subscriptionType = this.subscriptionType;
			return subscription;
		}
		
	}
	
	public enum SubscriptionType
	{
		subscribed,
		
		unconfigured,
		
		none
	}

}
