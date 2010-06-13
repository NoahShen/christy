package com.google.code.christy.dbhelper;

public class PubSubAffiliation
{
	private String serviceId;
	
	private String nodeId;
	
	private String jid;
	
	private AffiliationType affiliationType;


	/**
	 * @return the serviceId
	 */
	public String getServiceId()
	{
		return serviceId;
	}


	/**
	 * @return the nodeId
	 */
	public String getNodeId()
	{
		return nodeId;
	}


	/**
	 * @return the jid
	 */
	public String getJid()
	{
		return jid;
	}


	/**
	 * @return the affiliationType
	 */
	public AffiliationType getAffiliationType()
	{
		return affiliationType;
	}


	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(String serviceId)
	{
		this.serviceId = serviceId;
	}


	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(String nodeId)
	{
		this.nodeId = nodeId;
	}


	/**
	 * @param jid the jid to set
	 */
	public void setJid(String jid)
	{
		this.jid = jid;
	}


	/**
	 * @param affiliationType the affiliationType to set
	 */
	public void setAffiliationType(AffiliationType affiliationType)
	{
		this.affiliationType = affiliationType;
	}
	

	public enum AffiliationType
	{
		owner,
		
		publisher,
		
		outcast
	}
}
