package com.google.code.christy.cache;

public interface Cache
{
	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean exist(String key);

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key);

	/**
	 * 
	 * @param key
	 * @param obj
	 */
	public void put(String key, Object obj);

	/**
	 * 
	 * @param key
	 */
	public void remove(String key);
}
