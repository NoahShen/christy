package com.google.code.christy.dbhelper.db4odbhelpler;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.db4o.ObjectContainer;
import com.google.code.christy.dbhelper.OfflineSubscribeMsgDbHelper;
import com.google.code.christy.dbhelper.RosterItemDbHelper;
import com.google.code.christy.dbhelper.UserDbHelper;
import com.google.code.christy.dbhelper.UserPrivacyListDbHelper;
import com.google.code.christy.dbhelper.UserPrivacyListItemDbHelper;

public class Activator implements BundleActivator
{

	private ServiceRegistration rosterItemDbHelperRegistration;
	private ServiceRegistration userPrivacyListDbHelperRegistration;
	private ServiceRegistration userPrivacyListItemDbHelperRegistration;
	private ServiceRegistration offlineSubscribeMsgDbHelperRegistration;
	private ServiceRegistration userDbHelperRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		RosterItemDbHelperImpl rosterItemDbHelper = new RosterItemDbHelperImpl();
		rosterItemDbHelperRegistration = context.registerService(RosterItemDbHelper.class.getName(), rosterItemDbHelper, null);
	
		UserPrivacyListDbHelperImpl userPrivacyListDbHelper = new UserPrivacyListDbHelperImpl();
		userPrivacyListDbHelperRegistration = context.registerService(UserPrivacyListDbHelper.class.getName(), userPrivacyListDbHelper, null);
		
		UserPrivacyListItemDbHelperImpl userPrivacyListItemDbHelper = new UserPrivacyListItemDbHelperImpl();
		userPrivacyListItemDbHelperRegistration = context.registerService(UserPrivacyListItemDbHelper.class.getName(), userPrivacyListItemDbHelper, null);
	
		OfflineSubscribeMsgDbHelperImpl offlineSubscribeMsgDbHelper = new OfflineSubscribeMsgDbHelperImpl();
		offlineSubscribeMsgDbHelperRegistration = context.registerService(OfflineSubscribeMsgDbHelper.class.getName(), offlineSubscribeMsgDbHelper, null);
		
		UserDbHelperImpl userDbHelper = new UserDbHelperImpl();
		userDbHelperRegistration = context.registerService(UserDbHelper.class.getName(), userDbHelper, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.osgi.framework.BundleActivator#stop(org.osgi.framework.
	 * BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (rosterItemDbHelperRegistration != null)
		{
			rosterItemDbHelperRegistration.unregister();
			rosterItemDbHelperRegistration = null;
		}
		
		if (userPrivacyListDbHelperRegistration != null)
		{
			userPrivacyListDbHelperRegistration.unregister();
			userPrivacyListDbHelperRegistration = null;
		}
		
		if (userPrivacyListItemDbHelperRegistration != null)
		{
			userPrivacyListItemDbHelperRegistration.unregister();
			userPrivacyListItemDbHelperRegistration = null;
		}
		
		if (offlineSubscribeMsgDbHelperRegistration != null)
		{
			offlineSubscribeMsgDbHelperRegistration.unregister();
			offlineSubscribeMsgDbHelperRegistration = null;
		}
		
		if (userDbHelperRegistration != null)
		{
			userDbHelperRegistration.unregister();
			userDbHelperRegistration = null;
		}
		ObjectContainer oc = ObjectContainerInstance.getInstance();
		if (oc != null)
		{
			oc.close();
		}
	}

//	public static void main(String[] args) throws Exception
//	{
//		RosterItemDbHelperImpl rosterItemDbHelper = new RosterItemDbHelperImpl();
//		
//		net.sf.christy.sm.contactmgr.RosterItem rosterItem = new net.sf.christy.sm.contactmgr.RosterItem();
//		rosterItem.setUsername("Noah");
//		rosterItem.setRosterJID(new net.sf.christy.xmpp.JID("Noah.Shen87@gmail.com"));
//		rosterItem.setNickname("nickname");
//		rosterItem.setAsk(net.sf.christy.sm.contactmgr.RosterItem.Ask.subscribe);
//		rosterItem.setSubscription(net.sf.christy.sm.contactmgr.RosterItem.Subscription.both);
//		rosterItem.setGroups(new String[]{"group1", "group2"});
//		rosterItemDbHelper.addRosterItem(rosterItem);
//		
////		rosterItemDbHelper.updateRosterItemAsk("Noah", new JID("Noah.Shen87@gmail.com"), RosterItem.Ask.unsubscribe);
////		rosterItemDbHelper.updateRosterItemGroups("Noah", new JID("Noah.Shen87@gmail.com"), new String[]{"group"});
//		
////		rosterItemDbHelper.updateRosterItemNickname("Noah", new JID("Noah.Shen87@gmail.com"), "nickname2");
//		
////		rosterItemDbHelper.updateRosterItemSubscription("Noah", new JID("Noah.Shen87@gmail.com"), RosterItem.Subscription.none);
////		rosterItemDbHelper.removeRosterItem("Noah", new JID("Noah.Shen87@gmail.com"));
//		net.sf.christy.sm.contactmgr.RosterItem[] item = rosterItemDbHelper.getRosterItems("Noah");
//		System.out.println(java.util.Arrays.toString(item));
		
//		UserPrivacyListDbHelperImpl userPrivacyListDbHelper = new UserPrivacyListDbHelperImpl();
		
//		net.sf.christy.sm.privacy.UserPrivacyList list = new net.sf.christy.sm.privacy.UserPrivacyList();
//		list.setUsername("Noah");
//		list.setPrivacyName("privacyName");
//		list.setDefaultList(true);
//		userPrivacyListDbHelper.insertUserPrivacyList(list);
		
//		System.out.println(java.util.Arrays.toString(userPrivacyListDbHelper.getUserPrivacyLists("Noah")));
		
//		userPrivacyListDbHelper.cancelDefaultPrivacyList("Noah");
//		System.out.println(userPrivacyListDbHelper.getUserPrivacyList("Noah", "privacyName"));
//		userPrivacyListDbHelper.setDefaultPrivacyList("Noah", "privacyName");
//		net.sf.christy.sm.privacy.UserPrivacyList list = userPrivacyListDbHelper.getDefaultUserPrivacyList("Noah");
//		System.out.println(list);
//		list.setPrivacyName("p2");
		
//		final net.sf.christy.sm.privacy.UserPrivacyListItem item = new net.sf.christy.sm.privacy.UserPrivacyListItem();
//		item.setUsername("Noah");
//		item.setPrivacyName("p2");
//		item.setType(net.sf.christy.sm.privacy.UserPrivacyListItem.Type.subscription);
//		item.setValue("both");
//		item.setOrder(0);
//		item.setAction(net.sf.christy.sm.privacy.UserPrivacyListItem.Action.allow);
//		list.setItems(new java.util.ArrayList<net.sf.christy.sm.privacy.UserPrivacyListItem>(){{add(item);}});
//		
//		userPrivacyListDbHelper.updateUserPrivacyList(list);
		
//		UserPrivacyListItemDbHelperImpl userPrivacyListItemDbHelper = new UserPrivacyListItemDbHelperImpl();
//		final net.sf.christy.sm.privacy.UserPrivacyListItem item = new net.sf.christy.sm.privacy.UserPrivacyListItem();
//		item.setUsername("Noah");
//		item.setPrivacyName("p2");
//		item.setType(net.sf.christy.sm.privacy.UserPrivacyListItem.Type.subscription);
//		item.setValue("to");
//		item.setOrder(1);
//		item.setAction(net.sf.christy.sm.privacy.UserPrivacyListItem.Action.deny);
//		userPrivacyListItemDbHelper.insertUserPrivacyListItem(item);
//		System.out.println(java.util.Arrays.toString(userPrivacyListItemDbHelper.getUserPrivacyListItems("Noah", "p2")));
//		userPrivacyListItemDbHelper.deleteUserPrivacyListItem(userPrivacyListItemDbHelper.getUserPrivacyListItems("Noah", "p2")[0]);
		
		
//	}
}
