package net.sf.christy.router.consistenthashingbinder;

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
