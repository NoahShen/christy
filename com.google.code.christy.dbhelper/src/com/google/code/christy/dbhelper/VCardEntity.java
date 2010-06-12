package com.google.code.christy.dbhelper;

public class VCardEntity
{
	private String username;
	
	private String vCardContent;

	public VCardEntity()
	{
		super();
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getVCardContent()
	{
		return vCardContent;
	}

	public void setVCardContent(String cardContent)
	{
		vCardContent = cardContent;
	}
	
	
}
