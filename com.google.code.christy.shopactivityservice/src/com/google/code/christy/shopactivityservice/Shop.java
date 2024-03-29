package com.google.code.christy.shopactivityservice;

import java.util.HashMap;
import java.util.Map;

public class Shop
{
	private long shopId;
	
	private String eusername;
	
	private String type;
	
	private String title;
	
	private String content;
	
	private String shopImg;
	
	private String district;
	
	private String street;
	
	private String tel;

	private double latitude;
	
	private double longitude;
	
	private int easting;
	
	private int northing;
	
	private Double distanceFromUser;
	
	private Map<String, String> shopOverall = new HashMap<String, String>();
	
	private Map<Long, ShopComment> comments = new HashMap<Long, ShopComment>();
	
	public Shop()
	{
		super();
	}

	/**
	 * @return the shopId
	 */
	public long getShopId()
	{
		return shopId;
	}

	/**
	 * @return the eusername
	 */
	public String getEusername()
	{
		return eusername;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @return the shopImg
	 */
	public String getShopImg()
	{
		return shopImg;
	}

	/**
	 * @return the district
	 */
	public String getDistrict()
	{
		return district;
	}

	/**
	 * @return the street
	 */
	public String getStreet()
	{
		return street;
	}

	/**
	 * @return the tel
	 */
	public String getTel()
	{
		return tel;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude()
	{
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude()
	{
		return longitude;
	}

	/**
	 * @param shopId the shopId to set
	 */
	public void setShopId(long shopId)
	{
		this.shopId = shopId;
	}

	/**
	 * @param eusername the eusername to set
	 */
	public void setEusername(String eusername)
	{
		this.eusername = eusername;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 * @param shopImg the shopImg to set
	 */
	public void setShopImg(String shopImg)
	{
		this.shopImg = shopImg;
	}

	/**
	 * @param district the district to set
	 */
	public void setDistrict(String district)
	{
		this.district = district;
	}

	/**
	 * @param street the street to set
	 */
	public void setStreet(String street)
	{
		this.street = street;
	}

	/**
	 * @param tel the tel to set
	 */
	public void setTel(String tel)
	{
		this.tel = tel;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	/**
	 * @return the easting
	 */
	public int getEasting()
	{
		return easting;
	}

	/**
	 * @return the northing
	 */
	public int getNorthing()
	{
		return northing;
	}

	/**
	 * @param easting the easting to set
	 */
	public void setEasting(int easting)
	{
		this.easting = easting;
	}

	/**
	 * @param northing the northing to set
	 */
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

	public void addOverall(String name, String value)
	{
		shopOverall.put(name, value);
	}
	
	public boolean containOverall(String name)
	{
		return shopOverall.containsKey(name);
	}
	
	public void removeOverall(String name)
	{
		shopOverall.remove(name);
	}

	public Map<String, String> getShopOverall()
	{
		return shopOverall;
	}
	
	
	public boolean containComment(long commentId)
	{
		return comments.containsKey(commentId);
	}
	
	public void addComment(ShopComment comment)
	{
		if (!containComment(comment.getCommentId()))
		{
			comments.put(comment.getCommentId(), comment);
		}
	}
	
	public void removeComment(long commentId)
	{
		comments.remove(commentId);
	}

	public Map<Long, ShopComment> getComments()
	{
		return comments;
	}
	
}
