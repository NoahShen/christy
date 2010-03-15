package com.google.code.christy.sm;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.code.christy.log.LoggerServiceTracker;
import com.google.code.christy.sm.contactmgr.OfflineSubscribeMsgDbHelperTracker;
import com.google.code.christy.sm.contactmgr.RosterItemDbHelperTracker;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
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
		
		// TODO
		smManager.setName("sm_1");
		smManager.setDomain("example.com");
		smManager.setRouterIp("localhost");
		smManager.setRouterPassword("md5password");
		smManager.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
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
	}

}
