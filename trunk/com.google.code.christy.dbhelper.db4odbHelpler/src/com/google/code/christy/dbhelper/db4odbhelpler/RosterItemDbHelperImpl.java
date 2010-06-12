/**
 * 
 */
package com.google.code.christy.dbhelper.db4odbhelpler;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.google.code.christy.dbhelper.RosterItem;
import com.google.code.christy.dbhelper.RosterItemDbHelper;
import com.google.code.christy.dbhelper.RosterItem.Ask;
import com.google.code.christy.dbhelper.RosterItem.Subscription;
import com.google.code.christy.xmpp.JID;


/**
 * @author noah
 *
 */
public class RosterItemDbHelperImpl implements RosterItemDbHelper
{
	private ObjectContainer objectContainer;
	
	public RosterItemDbHelperImpl()
	{
		objectContainer = ObjectContainerInstance.getInstance();
		Db4o.configure().objectClass(RosterItem.class.getName()).cascadeOnDelete(true);
		Db4o.configure().objectClass(RosterItem.class.getName()).cascadeOnUpdate(true);
	}
	
	@Override
	public void addRosterItem(RosterItem rosterItem) throws Exception
	{
		
		objectContainer.set(rosterItem);
		objectContainer.commit();
	}

	@Override
	public RosterItem[] getRosterItems(final String username) throws Exception
	{
		ObjectSet<RosterItem> objSet = objectContainer.query(new Predicate<RosterItem>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 985534606769456822L;

			@Override
			public boolean match(RosterItem rosterItem)
			{
				if (rosterItem.getUsername().equals(username))
				{
					return true;
				}
				return false;
			}
			
		});
		
		// TODO test code;
		
		RosterItem item = new RosterItem();
		item.setUsername(username);
		item.setNickname("bNoah2");
		item.setGroups(new String[]{"group1", "group2"});
		item.setRosterJID(new JID("bNoah2", "example", null));
		item.setSubscription(RosterItem.Subscription.both);
		
		RosterItem item2 = new RosterItem();
		item2.setUsername(username);
		item2.setNickname("Noah2");
		item2.setRosterJID(new JID("Noah2", "example", null));
		item2.setSubscription(RosterItem.Subscription.both);
		return new RosterItem[]{item, item2};
//		return objSet.toArray(new RosterItem[]{});
	}

	@Override
	public void removeRosterItem(RosterItem rosterItem) throws Exception
	{
		objectContainer.delete(rosterItem);
		objectContainer.commit();
	}

	@Override
	public void removeRosterItem(final String username, final JID rosterJID) throws Exception
	{
		RosterItem rosterItem = getRosterItem(username, rosterJID);
		if (rosterItem != null)
		{
			objectContainer.delete(rosterItem);
			objectContainer.commit();
		}
		
	}

	@Override
	public RosterItem getRosterItem(final String username, final JID rosterJID) throws Exception
	{
		ObjectSet<RosterItem> objSet = objectContainer.query(new Predicate<RosterItem>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 985534606769456822L;

			@Override
			public boolean match(RosterItem rosterItem)
			{
				if (rosterItem.getUsername().equals(username)
						&& rosterItem.getRosterJID().equals(rosterJID))
				{
					return true;
				}
				return false;
			}
			
		});
		if (!objSet.isEmpty())
		{
			return objSet.get(0);
		}
		
		return null;
	}

	@Override
	public void updateRosterItem(RosterItem rosterItem) throws Exception
	{
		RosterItem oldRosterItem = getRosterItem(rosterItem.getUsername(), rosterItem.getRosterJID());
		if (oldRosterItem != null)
		{
			objectContainer.delete(oldRosterItem);
		}
		objectContainer.set(rosterItem);
		objectContainer.commit();
	}

	@Override
	public void updateRosterItemAsk(String username, JID rosterJID, Ask ask) throws Exception
	{
		RosterItem rosterItem = getRosterItem(username, rosterJID);
		if (rosterItem != null)
		{
			rosterItem.setAsk(ask);
			objectContainer.set(rosterItem);
			objectContainer.commit();
		}

	}

	@Override
	public void updateRosterItemGroups(String username, JID rosterJID, String[] groups) throws Exception
	{
		RosterItem rosterItem = getRosterItem(username, rosterJID);
		if (rosterItem != null)
		{
			rosterItem.setGroups(groups);
			objectContainer.set(rosterItem);
			objectContainer.commit();
		}
	}

	@Override
	public void updateRosterItemNickname(String username, JID rosterJID, String nickname) throws Exception
	{
		RosterItem rosterItem = getRosterItem(username, rosterJID);
		if (rosterItem != null)
		{
			rosterItem.setNickname(nickname);
			objectContainer.set(rosterItem);
			objectContainer.commit();
		}

	}

	@Override
	public void updateRosterItemSubscription(String username, JID rosterJID, Subscription subscription) throws Exception
	{
		RosterItem rosterItem = getRosterItem(username, rosterJID);
		if (rosterItem != null)
		{
			rosterItem.setSubscription(subscription);
			objectContainer.set(rosterItem);
			objectContainer.commit();
		}

	}

	
}
