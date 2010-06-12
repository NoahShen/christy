/**
 * 
 */
package com.google.code.christy.dbhelper;

import java.io.Serializable;

/**
 * @author noah
 *
 */
public class User implements Serializable
{


	/**
	 * 
	 */
	private static final long serialVersionUID = -5815493574645804466L;

	private String username;
	
	private String password;
	
	private long creationDate;
	
	private long modificationDate;

	/**
	 * 
	 */
	public User()
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
	 * @param username the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("username:").append(getUsername()).append("\n")
			.append("password:").append(getPassword()).append("\n")
			.append("creationDate:").append(getCreationDate()).append("\n")
			.append("modificationDate:").append(getModificationDate()).append("\n");
		
		return buf.toString();
	}
	
	
}
