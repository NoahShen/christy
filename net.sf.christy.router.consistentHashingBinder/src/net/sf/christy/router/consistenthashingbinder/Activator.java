package net.sf.christy.router.consistenthashingbinder;

import net.sf.christy.routemessageparser.RouteExtensionParser;
import net.sf.christy.router.ResourceBinder;
import net.sf.christy.router.RouterToSmInterceptor;
import net.sf.christy.router.consistenthashingbinder.parser.SearchRouteExtensionParser;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
	private HashFunctionServiceTracker hashFunctionServiceTracker;

	private ServiceRegistration md5HashFunctionRegistration;

	private ServiceRegistration consistentHashingResourceBinderRegistration;

	private ServiceRegistration consistentHashingRouterToSmInterceptorRegistration;

	private ServiceRegistration searchRouteExtensionParserRegistration;

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
		
		Md5HashFunction md5HashFunction = new Md5HashFunction();
		md5HashFunctionRegistration = context.registerService(HashFunction.class.getName(), md5HashFunction, null);

		hashFunctionServiceTracker = new HashFunctionServiceTracker(context);
		hashFunctionServiceTracker.open();

		ConsistentHashingResourceBinder consistentHashingResourceBinder = 
			new ConsistentHashingResourceBinder(hashFunctionServiceTracker, 50);// 50 replicas

		consistentHashingResourceBinderRegistration = 
			context.registerService(ResourceBinder.class.getName(), consistentHashingResourceBinder, null);
		
		consistentHashingRouterToSmInterceptorRegistration = 
			context.registerService(RouterToSmInterceptor.class.getName(), consistentHashingResourceBinder, null);
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
		
		if (md5HashFunctionRegistration != null)
		{
			md5HashFunctionRegistration.unregister();
			md5HashFunctionRegistration = null;
		}

		if (hashFunctionServiceTracker != null)
		{
			hashFunctionServiceTracker.close();
			hashFunctionServiceTracker = null;
		}

		if (consistentHashingResourceBinderRegistration != null)
		{
			consistentHashingResourceBinderRegistration.unregister();
			consistentHashingResourceBinderRegistration = null;
		}
		
		if (consistentHashingRouterToSmInterceptorRegistration != null)
		{
			consistentHashingRouterToSmInterceptorRegistration.unregister();
			consistentHashingRouterToSmInterceptorRegistration = null;
		}
	}

}
