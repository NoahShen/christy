package com.google.code.christy.xmpp.pubsub;

import java.util.ArrayList;
import java.util.List;

import com.google.code.christy.xmpp.XmlStanza;

public class PubSubPublish implements XmlStanza
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3158309357991683682L;

	private String node;
	
	private List<Item> items = new ArrayList<Item>();
	

	/**
	 * @param node
	 */
	public PubSubPublish(String node)
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

	@Override
	public String toXml()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<publish");
		
		if (getNode() != null)
		{
			buf.append(" node=\"").append(getNode()).append("\"");
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
			buf.append("</publish>");
		}
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PubSubPublish pubSubPublish = (PubSubPublish) super.clone();
		pubSubPublish.node = this.node;
		pubSubPublish.items = new ArrayList<Item>();
		for (Item item : this.items)
		{
			pubSubPublish.items.add((Item) item.clone());
		}
		
		return pubSubPublish;
	}
	

	public static class Item implements XmlStanza
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3904836584230079290L;

		private String id;
		
		private String payload;
		
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
