package com.google.code.christy.module.pubsub;

import org.apache.commons.configuration.XMLConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.log.LoggerServiceTracker;
import com.google.code.christy.module.pubsub.impl.PubSubManagerImpl;
import com.google.code.christy.routemessageparser.RouteMessageParserServiceTracker;

public class Activator implements BundleActivator
{

	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;
	private LoggerServiceTracker loggerServiceTracker;
	private ServiceRegistration pubSubManagerRegistration;

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
		
		PubSubManagerImpl pubSubManager = 
			new PubSubManagerImpl(loggerServiceTracker, 
					routeMessageParserServiceTracker);
		
		String appPath = System.getProperty("appPath");
		XMLConfiguration config = new XMLConfiguration(appPath + "/pusubconfig.xml");
		
		
		String domain = config.getString("domain", "pubsub.example.com");
		pubSubManager.setDomain(domain);

		String serviceId = config.getString("service-id", "123");
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
	}

}
