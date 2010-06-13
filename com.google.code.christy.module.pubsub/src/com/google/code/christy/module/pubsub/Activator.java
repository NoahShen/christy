package com.google.code.christy.module.pubsub;

import org.apache.commons.configuration.XMLConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.dbhelper.PubSubAffiliationDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubItemDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubNodeDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubSubscriptionDbHelperTracker;
import com.google.code.christy.log.LoggerServiceTracker;
import com.google.code.christy.module.pubsub.impl.PubSubManagerImpl;
import com.google.code.christy.routemessageparser.RouteMessageParserServiceTracker;

public class Activator implements BundleActivator
{

	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;
	private LoggerServiceTracker loggerServiceTracker;
	private ServiceRegistration pubSubManagerRegistration;
	private PubSubNodeDbHelperTracker pubSubNodeDbHelperTracker;
	private PubSubItemDbHelperTracker pubSubItemDbHelperTracker;
	private PubSubSubscriptionDbHelperTracker pubSubSubscriptionDbHelperTracker;
	private PubSubAffiliationDbHelperTracker pubSubAffiliationDbHelperTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		routeMessageParserServiceTracker = new RouteMessageParserServiceTracker(context);
		routeMessageParserServiceTracker.open();
		
		loggerServiceTracker = new LoggerServiceTracker(context);
		loggerServiceTracker.open();
		
		pubSubNodeDbHelperTracker = new PubSubNodeDbHelperTracker(context);
		pubSubNodeDbHelperTracker.open();
		
		pubSubItemDbHelperTracker = new PubSubItemDbHelperTracker(context);
		pubSubItemDbHelperTracker.open();
		
		pubSubSubscriptionDbHelperTracker = new PubSubSubscriptionDbHelperTracker(context);
		pubSubSubscriptionDbHelperTracker.open();
		
		pubSubAffiliationDbHelperTracker = new PubSubAffiliationDbHelperTracker(context);
		pubSubAffiliationDbHelperTracker.open();
		
		PubSubManagerImpl pubSubManager = 
			new PubSubManagerImpl(loggerServiceTracker, 
					routeMessageParserServiceTracker,
					pubSubNodeDbHelperTracker,
					pubSubItemDbHelperTracker,
					pubSubSubscriptionDbHelperTracker,
					pubSubAffiliationDbHelperTracker);
		
		String appPath = System.getProperty("appPath");
		XMLConfiguration config = new XMLConfiguration(appPath + "/pusubconfig.xml");
		
		
		String domain = config.getString("domain", "example.com");
		pubSubManager.setDomain(domain);

		String subDomain = config.getString("sub-domain");
		pubSubManager.setSubDomain(subDomain);
		
		String serviceId = config.getString("service-id");
		pubSubManager.setServiceId(serviceId);
		
		String routerIp = config.getString("router-ip", "localhost");
		pubSubManager.setRouterIp(routerIp);
		
		String routerPassword = config.getString("router-password", "md5password");
		pubSubManager.setRouterPassword(routerPassword);
		
		int routerPort = config.getInt("router-port", 8789);
		pubSubManager.setRouterPort(routerPort);
		
		pubSubManager.start();
		
		pubSubManagerRegistration = context.registerService(PubSubManager.class.getName(), pubSubManager, null);
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
		
		if (loggerServiceTracker != null)
		{
			loggerServiceTracker.close();
			loggerServiceTracker = null;
		}
		
		if (pubSubManagerRegistration != null)
		{
			pubSubManagerRegistration.unregister();
			pubSubManagerRegistration = null;
		}
		
		if (pubSubNodeDbHelperTracker != null)
		{
			pubSubNodeDbHelperTracker.close();
			pubSubNodeDbHelperTracker = null;
		}
		
		if (pubSubItemDbHelperTracker != null)
		{
			pubSubItemDbHelperTracker.close();
			pubSubItemDbHelperTracker = null;
		}
		
		if (pubSubSubscriptionDbHelperTracker != null)
		{
			pubSubSubscriptionDbHelperTracker.close();
			pubSubSubscriptionDbHelperTracker = null;
		}
		
		if (pubSubAffiliationDbHelperTracker != null)
		{
			pubSubAffiliationDbHelperTracker.close();
			pubSubAffiliationDbHelperTracker = null;
		}
	}

}
