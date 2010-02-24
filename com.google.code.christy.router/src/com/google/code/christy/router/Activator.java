package com.google.code.christy.router;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.code.christy.router.impl.RouteMessageParserServiceTracker;
import com.google.code.christy.router.impl.RouterManagerImpl;
import com.google.code.christy.router.impl.RouterToSmInterceptorServiceTracker;
import com.google.code.christy.router.impl.RouterToSmMessageDispatcherTracker;

public class Activator implements BundleActivator
{


	private RouterToSmMessageDispatcherTracker routerToSmMessageDispatcherTracker;
	private RouterToSmInterceptorServiceTracker routerToSmInterceptorServiceTracker;
	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{

		routerToSmMessageDispatcherTracker = new RouterToSmMessageDispatcherTracker(context);
		routerToSmMessageDispatcherTracker.open();

		routerToSmInterceptorServiceTracker = new RouterToSmInterceptorServiceTracker(context);
		routerToSmInterceptorServiceTracker.open();
		
		routeMessageParserServiceTracker = new RouteMessageParserServiceTracker(context);
		routeMessageParserServiceTracker.open();
		
		RouterManager rm = new RouterManagerImpl(routerToSmMessageDispatcherTracker, 
											routerToSmInterceptorServiceTracker,
											routeMessageParserServiceTracker);

		// TODO test code
		rm.setDomain("example.com");
		rm.registerSmModule("sm_1", "md5password");
		rm.registerSmModule("sm_1_1", "md5password");
		rm.registerC2sModule("c2s_1", "md5password");
		rm.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (routerToSmMessageDispatcherTracker != null)
		{
			routerToSmMessageDispatcherTracker.close();
			routerToSmMessageDispatcherTracker = null;
		}
		
		if (routerToSmInterceptorServiceTracker != null)
		{
			routerToSmInterceptorServiceTracker.close();
			routerToSmInterceptorServiceTracker = null;
		}
		
		if (routeMessageParserServiceTracker != null)
		{
			routeMessageParserServiceTracker.close();
			routeMessageParserServiceTracker = null;
		}
	}

}
