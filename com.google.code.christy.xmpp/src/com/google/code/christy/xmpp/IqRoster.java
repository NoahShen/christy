package com.google.code.christy.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.google.code.christy.util.StringUtils;




/**
 * Represents XMPP roster packets.
 * 
 * @author noah
 */
public class IqRoster implements PacketExtension
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8673323783374803789L;
	
	public static final String ELEMENTNAME = "query";
	
	public static final String NAMESPACE = "jabber:iq:roster";
	
	private final List<Item> rosterItems = new ArrayList<Item>();

	public IqRoster()
	{
	}

	/**
	 * Adds a roster item to the packet.
	 * 
	 * @param item
	 *                  a roster item.
	 */
	public void addRosterItem(Item item)
	{
		synchronized (rosterItems)
		{
			rosterItems.add(item);
		}
	}

	/**
	 * Returns the number of roster items in this roster packet.
	 * 
	 * @return the number of roster items.
	 */
	public int getRosterItemCount()
	{
		synchronized (rosterItems)
		{
			return rosterItems.size();
		}
	}

	/**
	 * Returns an unmodifiable collection for the roster items in the
	 * packet.
	 * 
	 * @return an unmodifiable collection for the roster items in the
	 *         packet.
	 */
	public Collection<Item> getRosterItems()
	{
		synchronized (rosterItems)
		{
			return Collections.unmodifiableList(new ArrayList<Item>(rosterItems));
		}
	}

	public Item getRosterItem(JID jid)
	{
		for (Item entry : rosterItems)
		{
			if (entry.getJid().equalsWithBareJid(jid))
			{
				return entry;
			}
		}
		return null;
	}
	
	public boolean containRosterItem(JID jid)
	{
		for (Item entry : rosterItems)
		{
			if (entry.getJid().equalsWithBareJid(jid))
			{
				return true;
			}
		}
		return false;
	}
	@Override
	public String getElementName()
	{
		return ELEMENTNAME;
	}

	@Override
	public String getNamespace()
	{
		return NAMESPACE;
	}

	@Override
	public String toXml()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<" + getElementName() + " xmlns=\"" + getNamespace() + "\">");
		synchronized (rosterItems)
		{
			for (Item entry : rosterItems)
			{
				buf.append(entry.toXml());
			}
		}
		buf.append("</" +  getElementName() + ">");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		IqRoster roster = (IqRoster) super.clone();
		for (Item item : rosterItems)
		{
			roster.addRosterItem((Item) item.clone());
		}
		
		return roster;
	}

	/**
	 * A roster item, which consists of a JID, their name, the type of
	 * subscription, and the groups the roster item belongs to.
	 */
	public static class Item implements XmlStanza
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 5584525791400570150L;

		private JID jid;

		private String name;

		private Subscription subscription;

		private Ask ask;

		private final Set<String> groupNames = new CopyOnWriteArraySet<String>();

		public Item(JID jid)
		{
			this.jid = jid;
		}

		/**
		 * Creates a new roster item.
		 * 
		 * @param jid
		 *                  the jid.
		 * @param name
		 *                  the user's name.
		 */
		public Item(JID jid, String name)
		{
			this.jid = jid;
			this.name = name;
			subscription = null;
			ask = null;

		}

		/**
		 * Returns the jid.
		 * 
		 * @return the jid.
		 */
		public JID getJid()
		{
			return jid;
		}

		/**
		 * Returns the user's name.
		 * 
		 * @return the user's name.
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the user's name.
		 * 
		 * @param name
		 *                  the user's name.
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Returns the roster subscription.
		 * 
		 * @return the roster subscription.
		 */
		public Subscription getSubscription()
		{
			return subscription;
		}

		/**
		 * Sets the roster subscription.
		 * 
		 * @param subscription
		 *                  the roster subscription.
		 */
		public void setSubscription(Subscription subscription)
		{
			this.subscription = subscription;
		}

		/**
		 * Returns the roster ask.
		 * 
		 * @return the roster ask.
		 */
		public Ask getAsk()
		{
			return ask;
		}

		/**
		 * Sets the roster item status.
		 * 
		 * @param itemStatus
		 *                  the roster item status.
		 */
		public void setAsk(Ask ask)
		{
			this.ask = ask;
		}

		/**
		 * Returns an unmodifiable set of the group names that the
		 * roster item belongs to.
		 * 
		 * @return group names.
		 */
		public String[] getGroupNames()
		{
			return groupNames.toArray(new String[]{});
		}

		/**
		 * 
		 * @param group
		 * @return
		 */
		public boolean containGroup(String group)
		{
			return groupNames.contains(group);
		}
		
		/**
		 * Adds a group name.
		 * 
		 * @param groupName
		 *                  the group name.
		 */
		public void addGroupName(String groupName)
		{
			if (groupName != null)
			{
				groupNames.add(groupName);
			}
		}

		/**
		 * Removes a group name.
		 * 
		 * @param groupName
		 *                  the group name.
		 */
		public void removeGroupName(String groupName)
		{
			groupNames.remove(groupName);
		}

		public void addGroups(String... groups)
		{
			for (String group : groups)
			{
				groupNames.add(group);
			}
		}
		
		public void removeGroups(String... groups)
		{
			for (String group : groups)
			{
				groupNames.remove(group);
			}
		}
		
		
		@Override
		public String toXml()
		{
			StringBuilder buf = new StringBuilder();
			buf.append("<item jid=\"").append(jid.toBareJID()).append("\"");
			if (name != null)
			{
				buf.append(" name=\"").append(StringUtils.escapeForXML(name)).append("\"");
			}
			if (subscription != null)
			{
				buf.append(" subscription=\"").append(subscription).append("\"");
			}
			if (ask != null)
			{
				buf.append(" ask=\"").append(ask).append("\"");
			}
			buf.append(">");
			for (String groupName : groupNames)
			{
				if (groupName != null && !groupName.isEmpty())
				{
					buf.append("<group>").append(StringUtils.escapeForXML(groupName)).append("</group>");
				}
				
			}
			buf.append("</item>");
			return buf.toString();
		}

		@Override
		public Object clone() throws CloneNotSupportedException
		{
			Item item = (Item) super.clone();
			item.jid = this.jid;
			item.name = this.name;
			item.ask = this.ask;
			item.subscription = this.subscription;
			item.groupNames.addAll(groupNames);
			
			return item;
		}
		
		
	}

	/**
	 * The subscription status of a roster item. An optional element that
	 * indicates the subscription status if a change request is pending.
	 */
	public static class Ask
	{

		/**
		 * Request to subcribe.
		 */
		public static final Ask SUBSCRIPTION_PENDING = new Ask("subscribe");

		/**
		 * Request to unsubscribe.
		 */
		public static final Ask UNSUBCRIPTION_PENDING = new Ask("unsubscribe");

		public static Ask fromString(String value)
		{
			if (value == null)
			{
				return null;
			}
			value = value.toLowerCase();
			if ("unsubscribe".equals(value))
			{
				return SUBSCRIPTION_PENDING;
			}
			else if ("subscribe".equals(value))
			{
				return SUBSCRIPTION_PENDING;
			}
			else
			{
				return null;
			}
		}

		private String value;

		/**
		 * Returns the item status associated with the specified
		 * string.
		 * 
		 * @param value
		 *                  the item status.
		 */
		private Ask(String value)
		{
			this.value = value;
		}

		public String toString()
		{
			return value;
		}
	}

	public enum Subscription
	{

		/**
		 * The user and subscriber have no interest in each other's
		 * presence.
		 */
		none,

		/**
		 * The user is interested in receiving presence updates from
		 * the subscriber.
		 */
		to,

		/**
		 * The subscriber is interested in receiving presence
		 * updates from the user.
		 */
		from,

		/**
		 * The user and subscriber have a mutual interest in each
		 * other's presence.
		 */
		both,

		/**
		 * The user wishes to stop receiving presence updates from
		 * the subscriber.
		 */
		remove
	}

}