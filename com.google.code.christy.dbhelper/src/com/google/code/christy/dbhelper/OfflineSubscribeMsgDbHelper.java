/**
 * 
 */
package com.google.code.christy.dbhelper;

/**
 * @author Noah
 *
 */
public interface OfflineSubscribeMsgDbHelper
{
	/**
	 * 
	 * @param offlineSubscribeMsg
	 * @throws Exception
	 */
	public void addOfflineSubscribeMsg(OfflineSubscribeMsg offlineSubscribeMsg) throws Exception;
	
	/**
	 * 
	 * @param offlineSubscribeMsg
	 * @throws Exception
	 */
	public void removeOfflineSubscribeMsg(OfflineSubscribeMsg offlineSubscribeMsg) throws Exception;
	
	/**
	 * 
	 * @param username
	 * @throws Exception
	 */
	public void removeOfflineSubscribeMsg(String username) throws Exception;
	
	
	/**
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public OfflineSubscribeMsg[] getOfflineSubscribeMsg(String username) throws Exception;
	
	/**
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public OfflineSubscribeMsg[] getAndRemoveOfflineSubscribeMsg(String username) throws Exception;
}
