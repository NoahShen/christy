/**
 * 
 */
package net.sf.christy.sm.contactmgr;

import java.io.Serializable;
import java.util.Arrays;

import net.sf.christy.xmpp.JID;

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
	
	private JID rosterJID;
	
	private String nickname;
	
	private Ask ask;
	
	private Subscription subscription;
	
	private String[] groups;
	
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
	public JID getRosterJID()
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
		return groups;
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
	public void setRosterJID(JID rosterJID)
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
		this.groups = groups;
	}

	@Override
	public String toString()
	{
		return "RosterItem [ask=" + ask + ", groups=" + Arrays.toString(groups) + ", nickname=" + nickname + ", rosterJID=" + rosterJID
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
