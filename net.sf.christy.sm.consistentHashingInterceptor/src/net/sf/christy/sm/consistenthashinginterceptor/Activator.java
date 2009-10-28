package net.sf.christy.sm.consistenthashinginterceptor;

import net.sf.christy.routemessageparser.RouteExtensionParser;
import net.sf.christy.sm.SmToRouterInterceptor;
import net.sf.christy.sm.consistenthashinginterceptor.parser.SearchRouteExtensionParser;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

	private ServiceRegistration searchRouteExtensionParserRegistration;
	private ServiceRegistration consistentHashingInterceptorRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		SearchRouteExtensionParser searchRouteExtensionParser = new SearchRouteExtensionParser();
		searchRouteExtensionParserRegistration = 
			context.registerService(RouteExtensionParser.class.getName(), searchRouteExtensionParser, null);
		
		ConsistentHashingInterceptor consistentHashingInterceptor = new ConsistentHashingInterceptor();
		consistentHashingInterceptorRegistration =
			context.registerService(SmToRouterInterceptor.class.getName(), consistentHashingInterceptor, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (searchRouteExtensionParserRegistration != null)
		{
			searchRouteExtensionParserRegistration.unregister();
			searchRouteExtensionParserRegistration = null;
		}
		
		if (consistentHashingInterceptorRegistration != null)
		{
			consistentHashingInterceptorRegistration.unregister();
			consistentHashingInterceptorRegistration = null;
		}
	}

}