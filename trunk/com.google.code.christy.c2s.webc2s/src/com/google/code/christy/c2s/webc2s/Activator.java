package com.google.code.christy.c2s.webc2s;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.c2s.C2SManager;

public class Activator implements BundleActivator
{

	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;

	private XmppParserServiceTracker xmppParserServiceTracker;

	private ServiceRegistration c2sManagerRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		routeMessageParserServiceTracker = new RouteMessageParserServiceTracker(context);
		routeMessageParserServiceTracker.open();

		xmppParserServiceTracker = new XmppParserServiceTracker(context);
		xmppParserServiceTracker.open();

		WebC2SManager c2sManager = new WebC2SManager(routeMessageParserServiceTracker, xmppParserServiceTracker);

		c2sManagerRegistration = context.registerService(C2SManager.class.getName(), c2sManager, null);

		// TODO
		c2sManager.setName("c2s_web1");
		c2sManager.setDomain("example.com");
		c2sManager.setRouterIp("localhost");
		c2sManager.setRouterPassword("md5password");
		c2sManager.start();
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

		if (xmppParserServiceTracker != null)
		{
			xmppParserServiceTracker.close();
			xmppParserServiceTracker = null;
		}

		if (c2sManagerRegistration != null)
		{
			c2sManagerRegistration.unregister();
			c2sManagerRegistration = null;
		}
	}

}
