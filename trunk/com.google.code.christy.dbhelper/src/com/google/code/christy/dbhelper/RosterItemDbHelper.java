/**
 * 
 */
package com.google.code.christy.dbhelper;


/**
 * @author noah
 *
 */
public interface RosterItemDbHelper
{
	/**
	 * 
	 * @param rosterItem
	 * @throws Exception
	 */
	public void addRosterItem(RosterItem rosterItem) throws Exception;
	
	/**
	 * 
	 * @param rosterItem
	 * @throws Exception
	 */
	public void removeRosterItem(RosterItem rosterItem) throws Exception;
	
	/**
	 * 
	 * @param username
	 * @param rosterJID
	 * @throws Exception
	 */
	public void removeRosterItem(String username, String rosterJID) throws Exception;
	
	/**
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public RosterItem[] getRosterItems(String username) throws Exception;
	
	
	/**
	 * 
	 * @param username
	 * @param rosterJID
	 * @return
	 * @throws Exception
	 */
	public RosterItem getRosterItem(String username, String rosterJID) throws Exception;
	
	/**
	 * 
	 * @param rosterItem
	 * @throws Exception
	 */
	public void updateRosterItem(RosterItem rosterItem) throws Exception;
	
	/**
	 * 
	 * @param username
	 * @param rosterJID
	 * @param nickname
	 * @throws Exception
	 */
	public void updateRosterItemNickname(String username, String rosterJID, String nickname) throws Exception;
	
	/**
	 * 
	 * @param username
	 * @param rosterJID
	 * @param subscription
	 * @throws Exception
	 */
	public void updateRosterItemSubscription(String username, String rosterJID, RosterItem.Subscription subscription) throws Exception;
	
	/**
	 * 
	 * @param username
	 * @param rosterJID
	 * @param ask
	 * @throws Exception
	 */
	public void updateRosterItemAsk(String username, String rosterJID, RosterItem.Ask ask) throws Exception;
	
	/**
	 * 
	 * @param username
	 * @param rosterJID
	 * @param groups
	 * @throws Exception
	 */
	public void updateRosterItemGroups(String username, String rosterJID, String[] groups) throws Exception;
}
