package com.google.code.christy.dbhelper.mysqldbhelpler;

import org.apache.commons.configuration.XMLConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.dbhelper.PrivateXmlDbHelper;
import com.google.code.christy.dbhelper.PubSubItemDbHelper;
import com.google.code.christy.dbhelper.PubSubNodeDbHelper;
import com.google.code.christy.dbhelper.RosterItemDbHelper;
import com.google.code.christy.dbhelper.UserDbHelper;
import com.google.code.christy.dbhelper.VCardDbHelper;
import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.log.LoggerServiceTracker;

public class Activator implements BundleActivator
{

	private ConnectionPool connPool;
	private ServiceRegistration rosterItemMysqlDbHelperRegistration;
	private ServiceRegistration userMysqlDbHelperRegistration;
	private ServiceRegistration privateXmlMysqlDbHelperRegistration;
	private ServiceRegistration vCardMysqlDbHelperRegistration;
	private ServiceRegistration pubSubNodeMysqlDbHelperRegistration;
	private LoggerServiceTracker loggerServiceTracker;
	private ServiceRegistration pubSubItemMysqlDbHelperRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		loggerServiceTracker = new LoggerServiceTracker(context);
		loggerServiceTracker.open();
		
		String appPath = System.getProperty("appPath");
		XMLConfiguration config = new XMLConfiguration(appPath + "/dbconfig.xml");		
		
		connPool = new ConnectionPool("com.mysql.jdbc.Driver",
				config.getString("url"),
				config.getString("user"),
				config.getString("password"));
		connPool.createPool();

		RosterItemMysqlDbHelper rosterItemMysqlDbHelper = new RosterItemMysqlDbHelper(connPool);
		rosterItemMysqlDbHelperRegistration = context.registerService(RosterItemDbHelper.class.getName(), rosterItemMysqlDbHelper, null);

		UserMysqlDbHelper userMysqlDbHelper = new UserMysqlDbHelper(connPool);
		userMysqlDbHelperRegistration = context.registerService(UserDbHelper.class.getName(), userMysqlDbHelper, null);
		
		PrivateXmlMysqlDbHelper privateXmlMysqlDbHelper = new PrivateXmlMysqlDbHelper(connPool);
		privateXmlMysqlDbHelperRegistration = context.registerService(PrivateXmlDbHelper.class.getName(), privateXmlMysqlDbHelper, null);
		
		VCardMysqlDbHelper vCardMysqlDbHelper = new VCardMysqlDbHelper(connPool);
		vCardMysqlDbHelperRegistration = context.registerService(VCardDbHelper.class.getName(), vCardMysqlDbHelper, null);
		
		PubSubNodeMysqlDbHelper pubSubNodeMysqlDbHelper = new PubSubNodeMysqlDbHelper(connPool, loggerServiceTracker);
		pubSubNodeMysqlDbHelperRegistration = context.registerService(PubSubNodeDbHelper.class.getName(), pubSubNodeMysqlDbHelper, null);
		
		
		PubSubItemMysqlDbHelper pubSubItemMysqlDbHelper = new PubSubItemMysqlDbHelper(connPool, loggerServiceTracker);
		pubSubItemMysqlDbHelperRegistration = context.registerService(PubSubItemDbHelper.class.getName(), pubSubItemMysqlDbHelper, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (loggerServiceTracker != null)
		{
			loggerServiceTracker.close();
			loggerServiceTracker = null;
		}
		
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
		
		if (privateXmlMysqlDbHelperRegistration != null)
		{
			privateXmlMysqlDbHelperRegistration.unregister();
			privateXmlMysqlDbHelperRegistration = null;
		}
		
		if (vCardMysqlDbHelperRegistration != null)
		{
			vCardMysqlDbHelperRegistration.unregister();
			vCardMysqlDbHelperRegistration = null;
		}
		
		if (pubSubNodeMysqlDbHelperRegistration != null)
		{
			pubSubNodeMysqlDbHelperRegistration.unregister();
			pubSubNodeMysqlDbHelperRegistration = null;
		}
		
		if (pubSubItemMysqlDbHelperRegistration != null)
		{
			pubSubItemMysqlDbHelperRegistration.unregister();
			pubSubItemMysqlDbHelperRegistration = null;
		}
	}
	
//	public static void main(String[] args) throws Exception
//	{
//		ConnectionPool connPool = new ConnectionPool("com.mysql.jdbc.Driver",
//							"jdbc:mysql://localhost/christy",
//							"root",
//							"123456");
//		connPool .createPool();
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
		
//		RosterItemMysqlDbHelper rosterItemMysqlDbHelper = new RosterItemMysqlDbHelper(connPool);
		
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
//	}
}
