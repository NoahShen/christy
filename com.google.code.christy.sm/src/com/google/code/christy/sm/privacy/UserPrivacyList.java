/**
 * 
 */
package com.google.code.christy.sm.privacy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author noah
 * 
 */
public class UserPrivacyList implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9215691869450225433L;

	private String username;

	private String privacyName;

	private boolean defaultList = false;

	private boolean activeList = false;

	private List<UserPrivacyListItem> items = new ArrayList<UserPrivacyListItem>();

	/**
	 * 
	 */
	public UserPrivacyList()
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
	 * @param username
	 *                  the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * @return the privacyName
	 */
	public String getPrivacyName()
	{
		return privacyName;
	}

	/**
	 * @param privacyName
	 *                  the privacyName to set
	 */
	public void setPrivacyName(String privacyName)
	{
		this.privacyName = privacyName;
	}

	/**
	 * @return the defaultList
	 */
	public boolean isDefaultList()
	{
		return defaultList;
	}

	/**
	 * @param defaultList
	 *                  the defaultList to set
	 */
	public void setDefaultList(boolean defaultList)
	{
		this.defaultList = defaultList;
	}

	/**
	 * @return the activeList
	 */
	public boolean isActiveList()
	{
		return activeList;
	}

	/**
	 * @param activeList
	 *                  the activeList to set
	 */
	public void setActiveList(boolean activeList)
	{
		this.activeList = activeList;
	}

	/**
	 * @return the items
	 */
	public List<UserPrivacyListItem> getItems()
	{
		return items;
	}

	/**
	 * @param items
	 *                  the items to set
	 */
	public void setItems(List<UserPrivacyListItem> items)
	{
		this.items = items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("username:").append(getUsername()).append("\n").append("privacyName:").append(getPrivacyName()).append("\n").append(
				"defaultList:").append(isDefaultList()).append("\n").append("activeList:").append(isActiveList()).append("\n");
		for (Iterator<UserPrivacyListItem> it = getItems().iterator(); it.hasNext();)
		{
			buf.append("   ").append(it.next().toString()).append("\n\n");
		}
		return buf.toString();
	}

}
