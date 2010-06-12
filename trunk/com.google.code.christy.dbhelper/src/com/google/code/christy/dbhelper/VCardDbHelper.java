package com.google.code.christy.dbhelper;


public interface VCardDbHelper
{
	/**
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public VCardEntity getVCardEntity(String username) throws Exception;
	
	/**
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void updateVCardEntity(VCardEntity entity) throws Exception;
	
	/**
	 * 
	 * @param entity
	 */
	public void removeVCardEntity(String username) throws Exception;
}
