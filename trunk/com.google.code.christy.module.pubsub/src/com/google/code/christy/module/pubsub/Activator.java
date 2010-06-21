package com.google.code.christy.module.pubsub;

import org.apache.commons.configuration.XMLConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.dbhelper.PubSubAffiliationDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubItemDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubNodeConfigDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubNodeDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubSubscriptionDbHelperTracker;
import com.google.code.christy.log.LoggerServiceTracker;
import com.google.code.christy.module.pubsub.impl.OpenAccessModel;
import com.google.code.christy.module.pubsub.impl.OpenPublisherModel;
import com.google.code.christy.module.pubsub.impl.PubSubManagerImpl;
import com.google.code.christy.module.pubsub.impl.AccessModelTracker;
import com.google.code.christy.module.pubsub.impl.PublisherModelTracker;
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
	private PubSubNodeConfigDbHelperTracker pubSubNodeConfigDbHelperTracker;
	private AccessModelTracker accessModelTracker;
	private ServiceRegistration openAccessModelRegistration;
	private ServiceRegistration openPublisherModelRegistration;
	private PublisherModelTracker publisherModelTracker;

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
		
		pubSubNodeConfigDbHelperTracker = new PubSubNodeConfigDbHelperTracker(context);
		pubSubNodeConfigDbHelperTracker.open();
		
		accessModelTracker = new AccessModelTracker(context);
		accessModelTracker.open();
		
		OpenAccessModel openAccessModel = new OpenAccessModel();
		openAccessModelRegistration = context.registerService(AccessModel.class.getName(), openAccessModel, null);
		
		publisherModelTracker = new PublisherModelTracker(context);
		publisherModelTracker.open();
		
		OpenPublisherModel openPublisherModel = new OpenPublisherModel();
		openPublisherModelRegistration = context.registerService(PublisherModel.class.getName(), openPublisherModel, null);
			
		PubSubManagerImpl pubSubManager = 
			new PubSubManagerImpl(loggerServiceTracker, 
					routeMessageParserServiceTracker,
					pubSubNodeDbHelperTracker,
					pubSubItemDbHelperTracker,
					pubSubSubscriptionDbHelperTracker,
					pubSubAffiliationDbHelperTracker,
					pubSubNodeConfigDbHelperTracker,
					accessModelTracker,
					publisherModelTracker);
		
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
		
		if (accessModelTracker != null)
		{
			accessModelTracker.close();
			accessModelTracker = null;
		}
		
		if (openAccessModelRegistration != null)
		{
			openAccessModelRegistration.unregister();
			openAccessModelRegistration = null;
		}
		
		if (publisherModelTracker != null)
		{
			publisherModelTracker.close();
			publisherModelTracker = null;
		}
		
		if (openPublisherModelRegistration != null)
		{
			openPublisherModelRegistration.unregister();
			openPublisherModelRegistration = null;
		}
	}

}
