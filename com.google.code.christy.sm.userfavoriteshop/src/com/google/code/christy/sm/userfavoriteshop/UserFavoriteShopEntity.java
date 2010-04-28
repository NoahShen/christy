package com.google.code.christy.sm.userfavoriteshop;

public class UserFavoriteShopEntity
{
	private long id;
	
	private String username;
	
	private long shopId;
	
	private String shopName;
	
	private String street;
	
	private String tel;

	public UserFavoriteShopEntity()
	{
		super();
	}

	/**
	 * @return the id
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public long getShopId()
	{
		return shopId;
	}

	public void setShopId(long shopId)
	{
		this.shopId = shopId;
	}

	public String getShopName()
	{
		return shopName;
	}

	public void setShopName(String shopName)
	{
		this.shopName = shopName;
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getTel()
	{
		return tel;
	}

	public void setTel(String tel)
	{
		this.tel = tel;
	}
	
	
}
