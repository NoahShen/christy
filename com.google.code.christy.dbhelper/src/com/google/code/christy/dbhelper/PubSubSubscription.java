package com.google.code.christy.dbhelper;

import java.util.HashSet;
import java.util.Set;

public class PubSubSubscription
{
	private String serviceId;
	
	private String nodeId;
	
	private String subId;
	
	private String jid;
	
	private String subscriber;

	private Subscription subscription;
	
	private boolean deliver;
	
	private boolean digest;
	
	private int digestFrequency;
	
	private int expire;
	
	private boolean includeBody;
	
	private Set<String> showValues = new HashSet<String>();
	
	private String subscriptionDepth;
	
	public PubSubSubscription()
	{
		super();
	}

	public String getServiceId()
	{
		return serviceId;
	}

	public void setServiceId(String serviceId)
	{
		this.serviceId = serviceId;
	}

	public String getNodeId()
	{
		return nodeId;
	}

	public void setNodeId(String nodeId)
	{
		this.nodeId = nodeId;
	}

	public String getSubId()
	{
		return subId;
	}

	public void setSubId(String subId)
	{
		this.subId = subId;
	}

	public String getJid()
	{
		return jid;
	}

	public void setJid(String jid)
	{
		this.jid = jid;
	}

	public String getSubscriber()
	{
		return subscriber;
	}

	public void setSubscriber(String subscriber)
	{
		this.subscriber = subscriber;
	}
	
	
	public Subscription getSubscription()
	{
		return subscription;
	}

	public void setSubscription(Subscription subscription)
	{
		this.subscription = subscription;
	}
		public boolean isDeliver()
	{
		return deliver;
	}

	public void setDeliver(boolean deliver)
	{
		this.deliver = deliver;
	}

	public boolean isDigest()
	{
		return digest;
	}

	public void setDigest(boolean digest)
	{
		this.digest = digest;
	}

	public int getDigestFrequency()
	{
		return digestFrequency;
	}

	public void setDigestFrequency(int digestFrequency)
	{
		this.digestFrequency = digestFrequency;
	}

	public int getExpire()
	{
		return expire;
	}

	public void setExpire(int expire)
	{
		this.expire = expire;
	}

	public boolean isIncludeBody()
	{
		return includeBody;
	}

	public void setIncludeBody(boolean includeBody)
	{
		this.includeBody = includeBody;
	}

	public Set<String> getShowValues()
	{
		return showValues;
	}

	public void setShowValues(Set<String> showValues)
	{
		this.showValues = showValues;
	}

	public String getSubscriptionDepth()
	{
		return subscriptionDepth;
	}

	public void setSubscriptionDepth(String subscriptionDepth)
	{
		this.subscriptionDepth = subscriptionDepth;
	}
	

	public enum Subscription
	{
		subscribed,
		
		unconfigured,
		
		none
	}



}
