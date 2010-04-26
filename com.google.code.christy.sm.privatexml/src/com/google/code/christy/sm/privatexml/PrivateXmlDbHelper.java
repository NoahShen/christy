package com.google.code.christy.sm.privatexml;

public interface PrivateXmlDbHelper
{
	/**
	 * 
	 * @param username
	 * @param stanzaKey
	 * @return
	 */
	public PrivateXmlEntity getPrivateXml(String username, String stanzaKey) throws Exception;
	
	/**
	 * 
	 * @param entity
	 * @throws Exception 
	 */
	public void updatePrivateXml(PrivateXmlEntity entity) throws Exception;
	
	/**
	 * 
	 * @param entity
	 */
	public void removePrivateXml(PrivateXmlEntity entity) throws Exception;
}
