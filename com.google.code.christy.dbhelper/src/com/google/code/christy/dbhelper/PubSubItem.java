package com.google.code.christy.dbhelper;

public class PubSubItem
{
	private String serviceId;
	
	private String nodeId;
	
	private String itemId;
	
	private String jid;
	
	private long creationDate;
	
	private String payload;

	public PubSubItem()
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

	public String getItemId()
	{
		return itemId;
	}

	public void setItemId(String itemId)
	{
		this.itemId = itemId;
	}

	public String getJid()
	{
		return jid;
	}

	public void setJid(String jid)
	{
		this.jid = jid;
	}

	public long getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(long creationDate)
	{
		this.creationDate = creationDate;
	}

	public String getPayload()
	{
		return payload;
	}

	public void setPayload(String payload)
	{
		this.payload = payload;
	}
	
	
}
