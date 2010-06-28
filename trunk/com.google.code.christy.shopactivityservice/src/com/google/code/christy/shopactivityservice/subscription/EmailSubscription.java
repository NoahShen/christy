package com.google.code.christy.shopactivityservice.subscription;

public class EmailSubscription
{
	private String username;
	
	private boolean receiveNewActivity;

	public EmailSubscription()
	{
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public boolean isReceiveNewActivity()
	{
		return receiveNewActivity;
	}

	public void setReceiveNewActivity(boolean receiveNewActivity)
	{
		this.receiveNewActivity = receiveNewActivity;
	}
	
	
}
