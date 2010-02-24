/**
 * 
 */
package com.google.code.christy.xmpp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author noah
 * 
 */
public class PrivacyList implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1158056361291009969L;

	/** Holds if it is an active list or not * */
	private boolean isActiveList;

	/** Holds if it is an default list or not * */
	private boolean isDefaultList;

	/** Holds the list name used to print * */
	private String listName;

	/** Holds the list of {@see PrivacyItem} * */
	private Set<PrivacyItem> items = new HashSet<PrivacyItem>();

	public PrivacyList(String listName)
	{
		super();
		this.listName = listName;
	}

	public boolean isActiveList()
	{
		return isActiveList;
	}

	public void setActiveList(boolean isActiveList)
	{
		this.isActiveList = isActiveList;
	}

	public boolean isDefaultList()
	{
		return isDefaultList;
	}

	public void setDefaultList(boolean isDefaultList)
	{
		this.isDefaultList = isDefaultList;
	}

	public String getListName()
	{
		return listName;
	}

	public void setListName(String listName)
	{
		this.listName = listName;
	}
	
	public void addItem(PrivacyItem item)
	{
		items.add(item);
	}
	
	public void removeItem(PrivacyItem item)
	{
		items.remove(item);
	}
	
	public PrivacyItem getItem(int order)
	{
		Iterator<PrivacyItem> it = items .iterator();
		while (it.hasNext())
		{
			PrivacyItem item = it.next();
			if (item.getOrder() == order)
				return item;
		}
		return null;
	}
	
	public Collection<PrivacyItem> getItems()
	{
		return Collections.unmodifiableCollection(items);
	}
	
	public String toXml()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<list");
		if (getListName() != null)
		{
			buf.append(" name=\"" + getListName() + "\"");
		}
		
		if (getItems().isEmpty())
		{
			buf.append("/>");
		}
		else
		{
			buf.append(">");
			Iterator<PrivacyItem> it = getItems().iterator();
			while (it.hasNext())
			{
				PrivacyItem item = it.next();
				buf.append(item.toXml());
			}
			buf.append("</list>");
		}
		
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PrivacyList list = (PrivacyList) super.clone();
		list.isActiveList = this.isActiveList;
		list.isDefaultList = this.isDefaultList;
		list.listName = this.listName;
		list.items = new HashSet<PrivacyItem>();
		for (PrivacyItem item : this.items)
		{
			list.addItem((PrivacyItem) item.clone());
		}
		
		return list;
	}
	
	
}
