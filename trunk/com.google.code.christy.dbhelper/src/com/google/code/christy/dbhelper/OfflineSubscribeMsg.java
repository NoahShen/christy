/**
 * 
 */
package com.google.code.christy.dbhelper;

import java.io.Serializable;


/**
 * @author Noah
 *
 */
public class OfflineSubscribeMsg implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6116470841908165934L;
	
	private String username;
	
	private String from;
	
	private String extensions;

	public OfflineSubscribeMsg()
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

	public String getFrom()
	{
		return from;
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public String getExtensions()
	{
		return extensions;
	}

	public void setExtensions(String extensions)
	{
		this.extensions = extensions;
	}
	
	
	
}
