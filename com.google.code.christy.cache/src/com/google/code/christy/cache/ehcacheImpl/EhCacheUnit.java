package com.google.code.christy.cache.ehcacheImpl;

import net.sf.ehcache.Element;

import com.google.code.christy.cache.Cache;

public class EhCacheUnit implements Cache
{
	private net.sf.ehcache.Cache ehCache;
	
	/**
	 * @param ehCache
	 */
	public EhCacheUnit(net.sf.ehcache.Cache ehCache)
	{
		this.ehCache = ehCache;
	}

	@Override
	public boolean exist(String key)
	{
		return ehCache.isKeyInCache(key);
	}

	@Override
	public Object get(String key)
	{
		Element elem = ehCache.get(key);
		if (elem != null)
		{
			return elem.getObjectValue();
		}
		return null;
	}

	@Override
	public void put(String key, Object obj)
	{
		ehCache.put(new Element(key, obj));
	}

	@Override
	public void remove(String key)
	{
		ehCache.remove(key);
	}

}
