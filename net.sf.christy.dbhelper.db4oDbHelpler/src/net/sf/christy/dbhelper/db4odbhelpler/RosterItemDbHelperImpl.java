/**
 * 
 */
package net.sf.christy.dbhelper.db4odbhelpler;

import com.db4o.Db4o;

import net.sf.christy.sm.contactmgr.RosterItem;
import net.sf.christy.sm.contactmgr.RosterItemDbHelper;
import net.sf.christy.sm.contactmgr.RosterItem.Ask;
import net.sf.christy.sm.contactmgr.RosterItem.Subscription;
import net.sf.christy.xmpp.JID;

/**
 * @author noah
 *
 */
public class RosterItemDbHelperImpl implements RosterItemDbHelper
{
	public RosterItemDbHelperImpl()
	{
		Db4o.configure().objectClass(RosterItem.class.getName()).cascadeOnDelete(true);
		Db4o.configure().objectClass(RosterItem.class.getName()).cascadeOnUpdate(true);
	}
	
	@Override
	public void addRosterItem(RosterItem rosterItem)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public RosterItem[] getRosterItems(String username)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeRosterItem(RosterItem rosterItem)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRosterItem(String username, JID rosterJID)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRosterItem(RosterItem rosterItem)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRosterItemAsk(String username, JID rosterJID, Ask ask)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRosterItemGroups(String username, JID rosterJID, String[] groups)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRosterItemNickname(String username, JID rosterJID, String nickname)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRosterItemSubscription(String username, JID rosterJID, Subscription subscription)
	{
		// TODO Auto-generated method stub

	}

}
