package com.google.code.christy.sm.consistenthashinginterceptor;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.sm.SmToRouterInterceptor;

public class Activator implements BundleActivator
{

	private ServiceRegistration consistentHashingInterceptorRegistration;


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{

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
		
		if (consistentHashingInterceptorRegistration != null)
		{
			consistentHashingInterceptorRegistration.unregister();
			consistentHashingInterceptorRegistration = null;
		}

	}

}
