/**
 * 
 */
package com.google.code.christy.sm;

import com.google.code.christy.util.Propertied;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.PrivacyList;


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
	public JID getBareJid();
	
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
	 * @return
	 */
	public UserResource[] getAllActiveUserResources();
	
	/**
	 * 
	 * @return
	 */
	public UserResource getMaxPriorityUserResource();
	
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
