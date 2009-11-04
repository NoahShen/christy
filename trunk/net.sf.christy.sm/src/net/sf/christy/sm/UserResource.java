/**
 * 
 */
package net.sf.christy.sm;

import net.sf.christy.util.Propertied;

/**
 * @author noah
 *
 */
public interface UserResource extends Propertied
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
	public String getRelatedC2s();
	
	/**
	 * 
	 * @return
	 */
	public String getResource();
	
	
}
