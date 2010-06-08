package com.google.code.christy.shopactivityservice;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.cache.Cache;
import com.google.code.christy.cache.CacheService;

public class CacheServiceTracker extends ServiceTracker
{

	public CacheServiceTracker(BundleContext context)
	{
		super(context, CacheService.class.getName(), null);
		// TODO Auto-generated constructor stub
	}

	public String[] getCacheNames()
	{
		Object obj = getService();
		if (obj != null)
		{
			CacheService cacheService = (CacheService) obj;
			return cacheService.getCacheNames();
		}
		return new String[]{};
	}
	
	public Cache getCache(String name)
	{
		Object obj = getService();
		if (obj != null)
		{
			CacheService cacheService = (CacheService) obj;
			return cacheService.getCache(name);
		}
		return null;
	}
}
