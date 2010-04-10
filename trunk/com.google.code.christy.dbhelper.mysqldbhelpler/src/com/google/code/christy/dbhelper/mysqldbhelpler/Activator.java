package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.util.Arrays;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.sm.contactmgr.RosterItem;
import com.google.code.christy.sm.contactmgr.RosterItemDbHelper;
import com.google.code.christy.sm.user.User;
import com.google.code.christy.sm.user.UserDbHelper;
import com.google.code.christy.xmpp.JID;

public class Activator implements BundleActivator
{

	private ConnectionPool connPool;
	private ServiceRegistration rosterItemMysqlDbHelperRegistration;
	private ServiceRegistration userMysqlDbHelperRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		connPool = new ConnectionPool("com.mysql.jdbc.Driver",
				"jdbc:mysql://localhost/christy",
				"root",
				"123456");
		connPool .createPool();

		RosterItemMysqlDbHelper rosterItemMysqlDbHelper = new RosterItemMysqlDbHelper(connPool);
		rosterItemMysqlDbHelperRegistration = context.registerService(RosterItemDbHelper.class.getName(), rosterItemMysqlDbHelper, null);

		UserMysqlDbHelper userMysqlDbHelper = new UserMysqlDbHelper(connPool);
		userMysqlDbHelperRegistration = context.registerService(UserDbHelper.class.getName(), userMysqlDbHelper, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (connPool != null)
		{
			connPool.closeConnectionPool();
		}
		
		if (rosterItemMysqlDbHelperRegistration != null)
		{
			rosterItemMysqlDbHelperRegistration.unregister();
			rosterItemMysqlDbHelperRegistration = null;
		}
		
		if (userMysqlDbHelperRegistration != null)
		{
			userMysqlDbHelperRegistration.unregister();
			userMysqlDbHelperRegistration = null;
		}
		
	}
	
	public static void main(String[] args) throws Exception
	{
		ConnectionPool connPool = new ConnectionPool("com.mysql.jdbc.Driver",
							"jdbc:mysql://localhost/christy",
							"root",
							"123456");
		connPool .createPool();
//		UserMysqlDbHelper userMysqlDbHelper = new UserMysqlDbHelper(connPool);
		
//		User user = new User();
//		user.setUsername("Noah");
//		user.setPassword("123456");
//		userMysqlDbHelper.addUser(user);
		
//		User user = userMysqlDbHelper.getUser("Noah");
//		String pwd = user.getPassword();
//		System.out.println(pwd);
		
//		userMysqlDbHelper.removeUser("Noah");
//		userMysqlDbHelper.updateUserPlainPassword("Noah", "123");
		
		RosterItemMysqlDbHelper rosterItemMysqlDbHelper = new RosterItemMysqlDbHelper(connPool);
		
//		RosterItem rosterItem = new RosterItem();
//		rosterItem.setUsername("Noah");
//		rosterItem.setRosterJID(new JID("Noah3", "example.com", null));
//		rosterItem.setNickname("NoahNickName34444");
//		rosterItem.setSubscription(RosterItem.Subscription.none);
//		rosterItem.setGroups(new String[]{"group111", "group555"});
//		rosterItemMysqlDbHelper.addRosterItem(rosterItem);
		
//		RosterItem[] items = rosterItemMysqlDbHelper.getRosterItems("Noah");
//		System.out.println(Arrays.toString(items));
		
//		rosterItemMysqlDbHelper.removeRosterItem("Noah", new JID("Noah2@example.com"));
//		RosterItem item = rosterItemMysqlDbHelper.getRosterItem("Noah", new JID("Noah2@example.com"));
//		System.out.println(item);
		
//		rosterItemMysqlDbHelper.updateRosterItem(rosterItem);
	}
}
