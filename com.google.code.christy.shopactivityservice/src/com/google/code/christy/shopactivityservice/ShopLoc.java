package com.google.code.christy.shopactivityservice;

public class ShopLoc
{
	private long shopId;
	
	private double latitude;
	
	private double longitude;

	public ShopLoc()
	{
		super();
	}

	public ShopLoc(long shopId, double latitude, double longitude)
	{
		super();
		this.shopId = shopId;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public long getShopId()
	{
		return shopId;
	}

	public void setShopId(long shopId)
	{
		this.shopId = shopId;
	}

	
}
