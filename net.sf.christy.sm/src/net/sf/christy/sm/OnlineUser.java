/**
 * 
 */
package net.sf.christy.sm;

import net.sf.christy.util.Propertied;

/**
 * @author noah
 *
 */
public interface OnlineUser extends Propertied
{
	/**
	 * 
	 * @return
	 */
	public String getNode();
	
	/**
	 * 
	 * @return
	 */
	public int getResourceCount();
	
	/**
	 * 
	 * @return
	 */
	public UserResource[] getAllUserResources();
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	public UserResource getUserResource(String resource);
}
