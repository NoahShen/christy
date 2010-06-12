package com.google.code.christy.dbhelper;

public class PrivateXmlEntity
{
	private String username;
	
	private String stanzaKey;
	
	private String stanzaXml;

	/**
	 * 
	 */
	public PrivateXmlEntity()
	{
	}

	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @return the stanzaKey
	 */
	public String getStanzaKey()
	{
		return stanzaKey;
	}

	/**
	 * @return the stanzaXml
	 */
	public String getStanzaXml()
	{
		return stanzaXml;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * @param stanzaKey the stanzaKey to set
	 */
	public void setStanzaKey(String stanzaKey)
	{
		this.stanzaKey = stanzaKey;
	}

	/**
	 * @param stanzaXml the stanzaXml to set
	 */
	public void setStanzaXml(String stanzaXml)
	{
		this.stanzaXml = stanzaXml;
	}
	
	
}
