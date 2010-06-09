package com.google.code.christy.sm;


import org.apache.commons.configuration.XMLConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.Christy;
import com.google.code.christy.ChristyTracker;
import com.google.code.christy.log.LoggerServiceTracker;
import com.google.code.christy.sm.contactmgr.OfflineSubscribeMsgDbHelperTracker;
import com.google.code.christy.sm.contactmgr.RosterItemDbHelperTracker;
import com.google.code.christy.sm.controller.SmController;
import com.google.code.christy.sm.impl.RouteMessageParserServiceTracker;
import com.google.code.christy.sm.impl.SmHandlerServiceTracker;
import com.google.code.christy.sm.impl.SmManagerImpl;
import com.google.code.christy.sm.impl.SmToRouterInterceptorServiceTracker;
import com.google.code.christy.sm.privacy.UserPrivacyListDbHelperTracker;
import com.google.code.christy.sm.user.UserDbHelperTracker;

public class Activator implements BundleActivator
{

	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;
	private SmToRouterInterceptorServiceTracker smToRouterInterceptorServiceTracker;
	private SmHandlerServiceTracker smHandlerServiceTracker;
	private UserPrivacyListDbHelperTracker userPrivacyListDbHelperTracker;
	private RosterItemDbHelperTracker rosterItemDbHelperTracker;
	private OfflineSubscribeMsgDbHelperTracker offlineSubscribeMsgDbHelperTracker;
	private UserDbHelperTracker userDbHelperTracker;
	private LoggerServiceTracker loggerServiceTracker;
	private SmController routerController;
	private ChristyTracker christyTracker;
	private ServiceRegistration smManagerRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		christyTracker = new ChristyTracker(context);
		christyTracker.open();
		
		Object service = christyTracker.getService();
		if (service == null)
		{
			throw new Exception("christy is null");
		}
		
		routeMessageParserServiceTracker = new RouteMessageParserServiceTracker(context);
		routeMessageParserServiceTracker.open();
		
		smToRouterInterceptorServiceTracker = new SmToRouterInterceptorServiceTracker(context);
		smToRouterInterceptorServiceTracker.open();
		
		smHandlerServiceTracker = new SmHandlerServiceTracker(context);
		smHandlerServiceTracker.open();
		
		userPrivacyListDbHelperTracker = new UserPrivacyListDbHelperTracker(context);
		userPrivacyListDbHelperTracker.open();
		
		rosterItemDbHelperTracker = new RosterItemDbHelperTracker(context);
		rosterItemDbHelperTracker.open();
		
		offlineSubscribeMsgDbHelperTracker = new OfflineSubscribeMsgDbHelperTracker(context);
		offlineSubscribeMsgDbHelperTracker.open();
		
		userDbHelperTracker = new UserDbHelperTracker(context);
		userDbHelperTracker.open();
		
		loggerServiceTracker = new LoggerServiceTracker(context);
		loggerServiceTracker.open();
		
		SmManagerImpl smManager = 
			new SmManagerImpl(routeMessageParserServiceTracker,
					smToRouterInterceptorServiceTracker,
					smHandlerServiceTracker,
					userPrivacyListDbHelperTracker,
					rosterItemDbHelperTracker,
					offlineSubscribeMsgDbHelperTracker,
					userDbHelperTracker,
					loggerServiceTracker);
		
		Christy christy = (Christy) service;
		XMLConfiguration config = (XMLConfiguration) christy.getProperty("config");
		
		String name = config.getString("name", "sm_1");
		smManager.setName(name);
		
		String domain = config.getString("domain", "example.com");
		smManager.setDomain(domain);
		
		String routerIp = config.getString("router-ip", "localhost");
		smManager.setRouterIp(routerIp);
		
		String routerPassword = config.getString("router-password", "md5password");
		smManager.setRouterPassword(routerPassword);
		
		int routerPort = config.getInt("router-port", 8789);
		smManager.setRouterPort(routerPort);
		
		int onlineUsersLimit = config.getInt("online-users-limit", 0);
		smManager.setOnlineUsersLimit(onlineUsersLimit);
		
		int resourceLimitPerUser = config.getInt("resource-limit-perUser", 0);
		smManager.setResourceLimitPerUser(resourceLimitPerUser);
		
		smManager.start();
		
		smManagerRegistration = context.registerService(SmManager.class.getName(), smManager, null);
		
		routerController = new SmController(smManager);
		routerController.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (christyTracker != null)
		{
			christyTracker.close();
			christyTracker = null;
		}
		
		if (routeMessageParserServiceTracker != null)
		{
			routeMessageParserServiceTracker.close();
			routeMessageParserServiceTracker = null;
		}
		
		if (smToRouterInterceptorServiceTracker != null)
		{
			smToRouterInterceptorServiceTracker.close();
			smToRouterInterceptorServiceTracker = null;
		}
		
		if (smHandlerServiceTracker != null)
		{
			smHandlerServiceTracker.close();
			smHandlerServiceTracker = null;
		}
		
		if (userPrivacyListDbHelperTracker != null)
		{
			userPrivacyListDbHelperTracker.close();
			userPrivacyListDbHelperTracker = null;
		}
		
		if (rosterItemDbHelperTracker != null)
		{
			rosterItemDbHelperTracker.close();
			rosterItemDbHelperTracker = null;
		}
		
		if (offlineSubscribeMsgDbHelperTracker != null)
		{
			offlineSubscribeMsgDbHelperTracker.close();
			offlineSubscribeMsgDbHelperTracker = null;
		}
		
		if (userDbHelperTracker != null)
		{
			userDbHelperTracker.close();
			userDbHelperTracker = null;
		}
		
		if (loggerServiceTracker != null)
		{
			loggerServiceTracker.close();
			loggerServiceTracker = null;
		}
		
		if (routerController != null)
		{
			routerController.stop();
			routerController = null;
		}
		
		if (smManagerRegistration != null)
		{
			smManagerRegistration.unregister();
			smManagerRegistration = null;
		}
	}

}
