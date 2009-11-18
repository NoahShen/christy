package net.sf.christy.dbhelper.db4odbhelpler;

import net.sf.christy.sm.contactmgr.RosterItemDbHelper;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

	private ServiceRegistration rosterItemDbHelperRegistration;

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
	}

//	public static void main(String[] args)
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
//	}
}
