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
	 */
	public void execCommand(Map<String, Object> params);
	
	/**
	 * 
	 */
	public void stopServer();
}
