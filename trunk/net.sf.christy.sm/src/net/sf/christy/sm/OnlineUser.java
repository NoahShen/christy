/**
 * 
 */
package net.sf.christy.sm;

import net.sf.christy.util.Propertied;
import net.sf.christy.xmpp.PrivacyList;

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
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	public boolean containUserResource(String resource);

	/**
	 * 
	 * @param streamId
	 * @return
	 */
	public UserResource getUserResourceByStreamId(String streamId);
	
	/**
	 * 
	 * @return
	 */
	public PrivacyList getDefaultPrivacyList();
}
