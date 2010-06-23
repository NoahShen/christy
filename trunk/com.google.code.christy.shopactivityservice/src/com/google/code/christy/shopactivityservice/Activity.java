package com.google.code.christy.shopactivityservice;

public class Activity
{
	private long activityId;
	
	private long shopId;
	
	private String title;
	
	private String intro;
	
	private String content;
	
	private String activityImg;
	
	private double longitude;
	
	private double latitude;
	
	private int easting;
	
	private int northing;
	
	private Double distanceFromUser;
	
	private long startDate;
	
	private long endDate;
	
	private long creationDate;
	
	private long modificationDate;

	public Activity()
	{
	}

	public long getActivityId()
	{
		return activityId;
	}

	public void setActivityId(long activityId)
	{
		this.activityId = activityId;
	}

	public long getShopId()
	{
		return shopId;
	}

	public void setShopId(long shopId)
	{
		this.shopId = shopId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getIntro()
	{
		return intro;
	}

	public void setIntro(String intro)
	{
		this.intro = intro;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getActivityImg()
	{
		return activityImg;
	}

	public void setActivityImg(String activityImg)
	{
		this.activityImg = activityImg;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
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

	public int getEasting()
	{
		return easting;
	}

	public void setEasting(int easting)
	{
		this.easting = easting;
	}

	public int getNorthing()
	{
		return northing;
	}

	public void setNorthing(int northing)
	{
		this.northing = northing;
	}

	public Double getDistanceFromUser()
	{
		return distanceFromUser;
	}

	public void setDistanceFromUser(Double distanceFromUser)
	{
		this.distanceFromUser = distanceFromUser;
	}

	public long getStartDate()
	{
		return startDate;
	}

	public void setStartDate(long startDate)
	{
		this.startDate = startDate;
	}

	public long getEndDate()
	{
		return endDate;
	}

	public void setEndDate(long endDate)
	{
		this.endDate = endDate;
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
	
	
}
