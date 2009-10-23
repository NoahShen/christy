package net.sf.christy.router;

import net.sf.christy.router.impl.ResourceBinderServiceTracker;
import net.sf.christy.router.impl.RouteMessageParserServiceTracker;
import net.sf.christy.router.impl.RouterManagerImpl;
import net.sf.christy.router.impl.RouterToSmInterceptorServiceTracker;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{


	private ResourceBinderServiceTracker resourceBinderServiceTracker;
	private RouterToSmInterceptorServiceTracker routerToSmInterceptorServiceTracker;
	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{

		resourceBinderServiceTracker = new ResourceBinderServiceTracker(context);
		resourceBinderServiceTracker.open();

		routerToSmInterceptorServiceTracker = new RouterToSmInterceptorServiceTracker(context);
		routerToSmInterceptorServiceTracker.open();
		
		routeMessageParserServiceTracker = new RouteMessageParserServiceTracker(context);
		routeMessageParserServiceTracker.open();
		
		RouterManager rm = new RouterManagerImpl(resourceBinderServiceTracker, 
											routerToSmInterceptorServiceTracker,
											routeMessageParserServiceTracker);

		// test code
		rm.setDomain("example.com");
		rm.registerSmModule("sm_1", "md5password");
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
		if (resourceBinderServiceTracker != null)
		{
			resourceBinderServiceTracker.close();
			resourceBinderServiceTracker = null;
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
