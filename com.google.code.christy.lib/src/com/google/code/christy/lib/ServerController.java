package com.google.code.christy.lib;

import java.util.Map;

public interface ServerController
{
	/**
	 * 
	 * @return
	 */
	public Map<String, Object> getServerInfo();
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	public Map<String, Object> execCommand(Map<String, Object> params);
	
	/**
	 * 
	 */
	public void stopServer();
}
