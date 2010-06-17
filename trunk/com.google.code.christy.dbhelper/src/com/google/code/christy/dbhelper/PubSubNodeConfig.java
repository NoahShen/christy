package com.google.code.christy.dbhelper;

public class PubSubNodeConfig
{
	private String serviceId;
	
	private String nodeId;
	
	private long creationDate;
	
	private long modificationDate;
	
	private boolean deliverPayloads;
	
	private int maxPayloadSize;
	
	private boolean persistItems;
	
	private int maxItems;
	
	private boolean notifyConfigChanges;
	
	private boolean notifyDelete;
	
	private boolean notifyRetract;
	
	private boolean sendItemSubscribe;
	
	private String publisherModel;
	
	private String subscribeModel;

	public PubSubNodeConfig()
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

	public long getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(long creationDate)
	{
		this.creationDate = creationDate;
	}

	public long getModificationDate()
	{
		return modificationDate;
	}

	public void setModificationDate(long modificationDate)
	{
		this.modificationDate = modificationDate;
	}

	public boolean isDeliverPayloads()
	{
		return deliverPayloads;
	}

	public void setDeliverPayloads(boolean deliverPayloads)
	{
		this.deliverPayloads = deliverPayloads;
	}

	public int getMaxPayloadSize()
	{
		return maxPayloadSize;
	}

	public void setMaxPayloadSize(int maxPayloadSize)
	{
		this.maxPayloadSize = maxPayloadSize;
	}

	public boolean isPersistItems()
	{
		return persistItems;
	}

	public void setPersistItems(boolean persistItems)
	{
		this.persistItems = persistItems;
	}

	public int getMaxItems()
	{
		return maxItems;
	}

	public void setMaxItems(int maxItems)
	{
		this.maxItems = maxItems;
	}

	public boolean isNotifyConfigChanges()
	{
		return notifyConfigChanges;
	}

	public void setNotifyConfigChanges(boolean notifyConfigChanges)
	{
		this.notifyConfigChanges = notifyConfigChanges;
	}

	public boolean isNotifyDelete()
	{
		return notifyDelete;
	}

	public void setNotifyDelete(boolean notifyDelete)
	{
		this.notifyDelete = notifyDelete;
	}

	public boolean isNotifyRetract()
	{
		return notifyRetract;
	}

	public void setNotifyRetract(boolean notifyRetract)
	{
		this.notifyRetract = notifyRetract;
	}

	public boolean isSendItemSubscribe()
	{
		return sendItemSubscribe;
	}

	public void setSendItemSubscribe(boolean sendItemSubscribe)
	{
		this.sendItemSubscribe = sendItemSubscribe;
	}

	public String getPublisherModel()
	{
		return publisherModel;
	}

	public void setPublisherModel(String publisherModel)
	{
		this.publisherModel = publisherModel;
	}

	public String getSubscribeModel()
	{
		return subscribeModel;
	}

	public void setSubscribeModel(String subscribeModel)
	{
		this.subscribeModel = subscribeModel;
	}

	
	
	
}
