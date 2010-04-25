package com.google.code.christy.shopactivityservice;

public class ShopVoter
{
	private long voterId;
	
	private String username;
	
	private long shopId;
	
	private String itemName;
	
	private int value;

	/**
	 * 
	 */
	public ShopVoter()
	{
	}

	/**
	 * @return the voterId
	 */
	public long getVoterId()
	{
		return voterId;
	}

	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @return the shopId
	 */
	public long getShopId()
	{
		return shopId;
	}

	/**
	 * @return the itemName
	 */
	public String getItemName()
	{
		return itemName;
	}

	/**
	 * @return the value
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * @param voterId the voterId to set
	 */
	public void setVoterId(long voterId)
	{
		this.voterId = voterId;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * @param shopId the shopId to set
	 */
	public void setShopId(long shopId)
	{
		this.shopId = shopId;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName)
	{
		this.itemName = itemName;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value)
	{
		this.value = value;
	}
	
	
}
