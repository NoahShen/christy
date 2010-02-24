/**
 * 
 */
package com.google.code.christy.sm.privacy;


/**
 * @author noah
 * 
 */
public interface UserPrivacyListDbHelper
{
	/**
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public UserPrivacyList[] getUserPrivacyLists(String username) throws Exception;

	/**
	 * 
	 * @param username
	 * @param privacyName
	 * @return
	 * @throws Exception
	 */
	public UserPrivacyList getUserPrivacyList(String username, String privacyName) throws Exception;

	/**
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public UserPrivacyList getDefaultUserPrivacyList(String username) throws Exception;

	/**
	 * 
	 * @param username
	 * @throws Exception
	 */
	public void cancelDefaultPrivacyList(String username) throws Exception;

	/**
	 * 
	 * @param userPrivacyList
	 * @throws Exception
	 */
	public void insertUserPrivacyList(UserPrivacyList userPrivacyList) throws Exception;

	/**
	 * 
	 * @param userPrivacyList
	 * @throws Exception
	 */
	public void updateUserPrivacyList(UserPrivacyList userPrivacyList) throws Exception;

	/**
	 * 
	 * @param username
	 * @param privacyName
	 * @throws Exception
	 */
	public void deleteUserPrivacyList(String username, String privacyName) throws Exception;

	/**
	 * 
	 * @param username
	 * @param privacyName
	 * @throws Exception
	 */
	public void setDefaultPrivacyList(String username, String privacyName) throws Exception;
}
