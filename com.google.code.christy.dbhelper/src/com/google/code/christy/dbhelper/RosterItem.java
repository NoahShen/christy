/**
 * 
 */
package com.google.code.christy.dbhelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author noah
 *
 */
public class RosterItem implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4736375390753772681L;
	
	private String username;
	
	private String rosterJID;
	
	private String nickname;
	
	private Ask ask;
	
	private Subscription subscription;
	
	private List<String> groups = new ArrayList<String>();
	
	/**
	 * 
	 */
	public RosterItem()
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
	 * @return the rosterJID
	 */
	public String getRosterJID()
	{
		return rosterJID;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname()
	{
		return nickname;
	}

	/**
	 * @return the ask
	 */
	public Ask getAsk()
	{
		return ask;
	}

	/**
	 * @return the subscription
	 */
	public Subscription getSubscription()
	{
		return subscription;
	}

	/**
	 * @return the groups
	 */
	public String[] getGroups()
	{
		return groups.toArray(new String[]{});
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * @param rosterJID the rosterJID to set
	 */
	public void setRosterJID(String rosterJID)
	{
		this.rosterJID = rosterJID;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	/**
	 * @param ask the ask to set
	 */
	public void setAsk(Ask ask)
	{
		this.ask = ask;
	}

	/**
	 * @param subscription the subscription to set
	 */
	public void setSubscription(Subscription subscription)
	{
		this.subscription = subscription;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(String[]  groups)
	{
		this.groups = new ArrayList<String>(Arrays.asList(groups));
	}
	
	public void addGroup(String group)
	{
		if (group != null)
		{
			this.groups.add(group);
		}
		
	}

	@Override
	public String toString()
	{
		return "RosterItem [ask=" + ask + ", groups=" + groups + ", nickname=" + nickname + ", rosterJID=" + rosterJID
				+ ", subscription=" + subscription + ", username=" + username + "]";
	}

	/**
	 * The subscription status of a roster item. An optional element that
	 * indicates the subscription status if a change request is pending.
	 */
	public enum Ask
	{
	
		subscribe,
		
		unsubscribe
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
