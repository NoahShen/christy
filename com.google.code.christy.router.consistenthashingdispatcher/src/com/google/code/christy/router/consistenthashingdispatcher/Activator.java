package com.google.code.christy.router.consistenthashingdispatcher;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.router.RouterToSmInterceptor;
import com.google.code.christy.router.RouterToSmMessageDispatcher;

public class Activator implements BundleActivator
{
	private HashFunctionServiceTracker hashFunctionServiceTracker;

	private ServiceRegistration md5HashFunctionRegistration;

	private ServiceRegistration consistentHashingDispatcherRegistration;

	private ServiceRegistration consistentHashingRouterToSmInterceptorRegistration;


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		
		
		Md5HashFunction md5HashFunction = new Md5HashFunction();
		md5HashFunctionRegistration = context.registerService(HashFunction.class.getName(), md5HashFunction, null);

		hashFunctionServiceTracker = new HashFunctionServiceTracker(context);
		hashFunctionServiceTracker.open();

		ConsistentHashingDispatcher consistentHashingDispatcher = 
			new ConsistentHashingDispatcher(hashFunctionServiceTracker, 50);// 50 replicas

		consistentHashingDispatcherRegistration = 
			context.registerService(RouterToSmMessageDispatcher.class.getName(), consistentHashingDispatcher, null);
		
		consistentHashingRouterToSmInterceptorRegistration = 
			context.registerService(RouterToSmInterceptor.class.getName(), consistentHashingDispatcher, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{

		
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

		if (consistentHashingDispatcherRegistration != null)
		{
			consistentHashingDispatcherRegistration.unregister();
			consistentHashingDispatcherRegistration = null;
		}
		
		if (consistentHashingRouterToSmInterceptorRegistration != null)
		{
			consistentHashingRouterToSmInterceptorRegistration.unregister();
			consistentHashingRouterToSmInterceptorRegistration = null;
		}
	}

}
