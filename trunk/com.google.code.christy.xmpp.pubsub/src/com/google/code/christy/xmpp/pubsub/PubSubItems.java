package com.google.code.christy.xmpp.pubsub;

import java.util.ArrayList;
import java.util.List;

import com.google.code.christy.xmpp.XmlStanza;

public class PubSubItems implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2413440381715184839L;

	private String node;
	
	private String subId;
	
	private int maxItems;
	
	private List<Item> items = new ArrayList<Item>();
	
	/**
	 * @param node
	 */
	public PubSubItems(String node)
	{
		this.node = node;
	}

	public void addItem(Item item)
	{
		items.add(item);
	}
	
	public void removeItem(Item item)
	{
		items.remove(item);
	}
	
	public List<Item> getItems()
	{
		return items;
	}
	
	/**
	 * @return the node
	 */
	public String getNode()
	{
		return node;
	}

	/**
	 * @return the maxItems
	 */
	public int getMaxItems()
	{
		return maxItems;
	}

	/**
	 * @param maxItems the maxItems to set
	 */
	public void setMaxItems(int maxItems)
	{
		this.maxItems = maxItems;
	}

	/**
	 * @return the subId
	 */
	public String getSubId()
	{
		return subId;
	}

	/**
	 * @param subId the subId to set
	 */
	public void setSubId(String subId)
	{
		this.subId = subId;
	}

	@Override
	public String toXml()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<items");
		
		if (getNode() != null)
		{
			buf.append(" node=\"").append(getNode()).append("\"");
		}
		
		if (getMaxItems() != 0)
		{
			buf.append(" max_items=\"").append(getMaxItems()).append("\"");
		}
		
		if (getSubId() != null)
		{
			buf.append(" subid=\"").append(getSubId()).append("\"");
		}
		
		if (items.isEmpty())
		{
			buf.append("/>");
		}
		else
		{
			buf.append(">");
			for (Item item : items)
			{
				buf.append(item.toXml());
			}
			buf.append("</items>");
		}
		return buf.toString();
		
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PubSubItems pubSubItems = (PubSubItems) super.clone();
		pubSubItems.node = this.node;
		pubSubItems.items = new ArrayList<Item>();
		for (Item item : this.items)
		{
			pubSubItems.items.add((Item) item.clone());
		}
		
		return pubSubItems;
	}

	public static class Item implements XmlStanza
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3904836584230079290L;

		private String id;
		
		private String payload;
		
		public Item()
		{
			super();
		}

		/**
		 * @param id
		 */
		public Item(String id)
		{
			this.id = id;
		}

		/**
		 * @return the id
		 */
		public String getId()
		{
			return id;
		}


		/**
		 * @return the payload
		 */
		public String getPayload()
		{
			return payload;
		}

		/**
		 * @param payload the payload to set
		 */
		public void setPayload(String payload)
		{
			this.payload = payload;
		}

		@Override
		public String toXml()
		{
			StringBuilder buf = new StringBuilder();
			buf.append("<item");
			
			if (getId() != null)
			{
				buf.append(" id=\"").append(getId()).append("\"");
			}
			
			if (getPayload() == null)
			{
				buf.append("/>");
			}
			else
			{
				buf.append(">");
				buf.append(getPayload());
				buf.append("</item>");
			}
			return buf.toString();
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException
		{
			Item item = (Item) super.clone();
			item.id = this.id;
			item.payload = this.payload;
			
			return item;
		}
		
	}
}
