package com.google.code.christy.cache;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.cache.ehcacheImpl.EhcacheService;

public class Activator implements BundleActivator
{

	private ServiceRegistration ehcacheServiceRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		String appPath = System.getProperty("appPath");
		EhcacheService ehcacheService = new EhcacheService(appPath + "/ehcache.xml");
		
		ehcacheServiceRegistration = context.registerService(CacheService.class.getName(), ehcacheService, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (ehcacheServiceRegistration != null)
		{
			ehcacheServiceRegistration.unregister();
			ehcacheServiceRegistration = null;
		}
	}

}
