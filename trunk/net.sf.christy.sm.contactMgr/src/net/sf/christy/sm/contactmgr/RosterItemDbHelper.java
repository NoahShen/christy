/**
 * 
 */
package net.sf.christy.sm.contactmgr;

import net.sf.christy.xmpp.JID;

/**
 * @author noah
 *
 */
public interface RosterItemDbHelper
{
	public void addRosterItem(RosterItem rosterItem);
	
	public void removeRosterItem(RosterItem rosterItem);
	
	public void removeRosterItem(String username, JID rosterJID);
	
	public RosterItem[] getRosterItems(String username);
	
	public void updateRosterItem(RosterItem rosterItem);
	
	public void updateRosterItemNickname(String username, JID rosterJID, String nickname);
	
	public void updateRosterItemSubscription(String username, JID rosterJID, RosterItem.Subscription subscription);
	
	public void updateRosterItemAsk(String username, JID rosterJID, RosterItem.Ask ask);
	
	public void updateRosterItemGroups(String username, JID rosterJID, String[] groups);
}
