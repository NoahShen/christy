/**
 * 
 */
package net.sf.christy.sm.privacy;

import java.io.Serializable;

/**
 * @author noah
 * 
 */
public class UserPrivacyListItem implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5610637756940835030L;

	private String username;

	private String privacyName;

	private int order;

	private Type type;

	private String value;

	private Action action;

	private boolean messageFilter;

	private boolean presenceInFilter;

	private boolean presenceOutFilter;

	private boolean iqFilter;

	/**
	 * 
	 */
	public UserPrivacyListItem()
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
	 * @return the order
	 */
	public int getOrder()
	{
		return order;
	}

	/**
	 * @param order
	 *                  the order to set
	 */
	public void setOrder(int order)
	{
		this.order = order;
	}

	/**
	 * @return the type
	 */
	public Type getType()
	{
		return type;
	}

	/**
	 * @param type
	 *                  the type to set
	 */
	public void setType(Type type)
	{
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *                  the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @return the action
	 */
	public Action getAction()
	{
		return action;
	}

	/**
	 * @param action
	 *                  the action to set
	 */
	public void setAction(Action action)
	{
		this.action = action;
	}

	/**
	 * @return the messageFilter
	 */
	public boolean isMessageFilter()
	{
		return messageFilter;
	}

	/**
	 * @param messageFilter
	 *                  the messageFilter to set
	 */
	public void setMessageFilter(boolean messageFilter)
	{
		this.messageFilter = messageFilter;
	}

	/**
	 * @return the presenceInFilter
	 */
	public boolean isPresenceInFilter()
	{
		return presenceInFilter;
	}

	/**
	 * @param presenceInFilter
	 *                  the presenceInFilter to set
	 */
	public void setPresenceInFilter(boolean presenceInFilter)
	{
		this.presenceInFilter = presenceInFilter;
	}

	/**
	 * @return the presenceOutFilter
	 */
	public boolean isPresenceOutFilter()
	{
		return presenceOutFilter;
	}

	/**
	 * @param presenceOutFilter
	 *                  the presenceOutFilter to set
	 */
	public void setPresenceOutFilter(boolean presenceOutFilter)
	{
		this.presenceOutFilter = presenceOutFilter;
	}

	/**
	 * @return the iqFilter
	 */
	public boolean isIqFilter()
	{
		return iqFilter;
	}

	/**
	 * @param iqFilter
	 *                  the iqFilter to set
	 */
	public void setIqFilter(boolean iqFilter)
	{
		this.iqFilter = iqFilter;
	}

	public static enum Action
	{
		allow,

		deny
	}

	public static enum Type
	{
		/**
		 * JID being analyzed should belong to a roster group of the
		 * list's owner.
		 */
		group,
		/**
		 * JID being analyzed should have a resource match, domain
		 * match or bare JID match.
		 */
		jid,
		/**
		 * JID being analyzed should belong to a contact present in
		 * the owner's roster with the specified subscription
		 * status.
		 */
		subscription
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
		buf.append("username:").append(getUsername()).append("  ").append("privacyName:").append(getPrivacyName()).append("  ").append(
				"order:").append(getOrder()).append("  ").append("type:").append(getType()).append("  ").append("value:")
				.append(getValue()).append("  ").append("action:").append(getAction()).append("  ")
				.append("isMessageFilter:").append(isMessageFilter()).append("  ").append("isPresenceInFilter:").append(
						isPresenceInFilter()).append("  ").append("isPresenceOutFilter:").append(
						isPresenceOutFilter()).append("  ").append("isIQFiter:").append(isIqFilter())
				.append("  ");

		return buf.toString();
	}

}
