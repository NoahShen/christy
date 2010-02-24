package com.google.code.christy.router.consistenthashingdispatcher;

/**
 * 
 * @author noah
 *
 */
public interface HashFunction
{
	/**
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public int hash(Object obj);
}
