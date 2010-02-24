/**
 * 
 */
package com.google.code.christy.sm.privacy;


/**
 * @author noah
 *
 */
public interface UserPrivacyListItemDbHelper
{
	/**
	 * 
	 * @param userPrivacyList
	 * @return
	 * @throws Exception
	 */
	public UserPrivacyListItem[] getUserPrivacyListItems(UserPrivacyList userPrivacyList) throws Exception;
	
	/**
	 * 
	 * @param username
	 * @param privacyName
	 * @return
	 * @throws Exception
	 */
	public UserPrivacyListItem[] getUserPrivacyListItems(String username, String privacyName) throws Exception;
	
	/**
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void insertUserPrivacyListItem(UserPrivacyListItem item) throws Exception;
	
	/**
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateUserPrivacyListItem(UserPrivacyListItem item) throws Exception;
	
	/**
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void deleteUserPrivacyListItem(UserPrivacyListItem item) throws Exception;
}
