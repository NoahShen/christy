/**
 * 
 */
package com.google.code.christy.dbhelper;

import java.io.Serializable;

import com.google.code.christy.xmpp.JID;


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
	
	private JID from;
	
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

	public JID getFrom()
	{
		return from;
	}

	public void setFrom(JID from)
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
