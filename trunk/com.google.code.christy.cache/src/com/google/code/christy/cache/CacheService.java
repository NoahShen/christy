package com.google.code.christy.cache;


public interface CacheService
{
	/**
	 * 
	 * @return
	 */
	public String[] getCacheNames();
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Cache getCache(String name);
}
