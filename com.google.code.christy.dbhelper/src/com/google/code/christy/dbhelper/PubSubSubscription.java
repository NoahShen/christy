package com.google.code.christy.dbhelper;

public class PubSubSubscription
{
	private String serviceId;
	
	private String nodeId;
	
	private String subId;
	
	private String jid;
	
	private String subscriber;

	private Subscription subscription;
	
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
	
	public enum Subscription
	{
		subscribed,
		
		unconfigured,
		
		none
	}

}
