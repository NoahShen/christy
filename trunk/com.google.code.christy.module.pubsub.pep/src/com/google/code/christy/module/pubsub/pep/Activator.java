package com.google.code.christy.module.pubsub.pep;

import org.apache.commons.configuration.XMLConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.dbhelper.RosterItemDbHelperTracker;
import com.google.code.christy.log.LoggerServiceTracker;
import com.google.code.christy.module.pubsub.PubSubManager;
import com.google.code.christy.routemessageparser.RouteMessageParserServiceTracker;

public class Activator implements BundleActivator
{

	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;
	private LoggerServiceTracker loggerServiceTracker;
	private ServiceRegistration pubSubManagerRegistration;
	private RosterItemDbHelperTracker rosterItemDbHelperTracker;

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
		
		
		rosterItemDbHelperTracker = new RosterItemDbHelperTracker(context);
		rosterItemDbHelperTracker.open();
		
		PEPPubSubManager pubSubManager = 
			new PEPPubSubManager(loggerServiceTracker, 
					routeMessageParserServiceTracker,
					rosterItemDbHelperTracker);
		
		String appPath = System.getProperty("appPath");
		XMLConfiguration config = new XMLConfiguration(appPath + "/pusubconfig.xml");
		
		
		String domain = config.getString("domain", "example.com");
		pubSubManager.setDomain(domain);

		String subDomain = config.getString("sub-domain", "pep.example.com");
		pubSubManager.setSubDomain(subDomain);
		
		String serviceId = config.getString("service-id");
		pubSubManager.setServiceId(serviceId);
		
		String routerIp = config.getString("router-ip", "localhost");
		pubSubManager.setRouterIp(routerIp);
		
		String routerPassword = config.getString("router-password", "md5password");
		pubSubManager.setRouterPassword(routerPassword);
		
		int routerPort = config.getInt("router-port", 8789);
		pubSubManager.setRouterPort(routerPort);
		
		int maxItems = config.getInt("max-items", 10);
		pubSubManager.setMaxItems(maxItems);

		
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
		
		if (rosterItemDbHelperTracker != null)
		{
			rosterItemDbHelperTracker.close();
			rosterItemDbHelperTracker = null;
		}
	}

}