package com.google.code.christy.cache.ehcacheImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.CacheManager;

import com.google.code.christy.cache.Cache;
import com.google.code.christy.cache.CacheService;


public class EhcacheService implements CacheService
{
	private CacheManager manager;
	
	private Map<String, EhCacheUnit> caches = new ConcurrentHashMap<String, EhCacheUnit>();

	public EhcacheService(String configUrl)
	{
		manager = new CacheManager(configUrl);
		for (String name : manager.getCacheNames())
		{
			net.sf.ehcache.Cache ehCache = manager.getCache(name);
			EhCacheUnit cacheUnit = new EhCacheUnit(ehCache);
			caches.put(name, cacheUnit);
		}
	}
	
	public String[] getCacheNames()
	{
		return manager.getCacheNames();
	}
	
	public Cache getCache(String name)
	{
		return caches.get(name);
	}

}
