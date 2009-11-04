package net.sf.christy.sm;

import net.sf.christy.sm.impl.RouteMessageParserServiceTracker;
import net.sf.christy.sm.impl.SmManagerImpl;
import net.sf.christy.sm.impl.SmToRouterInterceptorServiceTracker;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;
	private SmToRouterInterceptorServiceTracker smToRouterInterceptorServiceTracker;

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
		
		SmManagerImpl smManager = 
			new SmManagerImpl(routeMessageParserServiceTracker,smToRouterInterceptorServiceTracker);
		
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
	}

}
