package com.google.code.christy.util.collections;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6663997823899326869L;

	private final int maxCapacity;

	private static final float DEFAULT_LOAD_FACTOR = 0.75f;

	private final Lock lock = new ReentrantLock();

	public LRULinkedHashMap(int maxCapacity)
	{
		super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
		this.maxCapacity = maxCapacity;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest)
	{
		return size() > maxCapacity;
	}

	/**
	 * Weather contains someone Object according to a key
	 * 
	 * @param key
	 */
	@Override
	public boolean containsKey(Object key)
	{
		lock.lock();
		try
		{
			return super.containsKey(key);
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Get one Object according to a key
	 * 
	 * @param key
	 */
	@Override
	public V get(Object key)
	{
		lock.lock();
		try
		{
			return super.get(key);
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Put an Object to cache
	 * 
	 * @param key
	 *                  ,valueӻ���
	 */
	@Override
	public V put(K key, V value)
	{
		lock.lock();
		try
		{
			return super.put(key, value);
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * The cache's size
	 */
	public int size()
	{
		lock.lock();
		try
		{
			return super.size();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * clear the cache
	 * 
	 */
	public void clear()
	{
		lock.lock();
		try
		{
			super.clear();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Delete one Object from cache
	 * 
	 * @param key
	 */
	public V remove(Object key)
	{
		lock.lock();
		try
		{
			return super.remove(key);
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Update one Object in the Cache
	 * 
	 * @param key
	 * @param value
	 */
	public void update(K key, V value)
	{
		lock.lock();
		try
		{
			super.remove(key);
			this.put(key, value);
		}
		finally
		{
			lock.unlock();
		}
	}

}
