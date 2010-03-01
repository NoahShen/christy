/**
 * 
 */
package com.google.code.christy.c2s;

import com.google.code.christy.util.Propertied;

/**
 * @author noah
 *
 */
public interface C2SManager extends Propertied
{
	public static final String C2SROUTER_NAMESPACE = "christy:internal:c2s2router";
	
	public static final String C2SROUTER_AUTH_NAMESPACE = "christy:internal:c2s2router:auth";
	
	/**
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * max client connection number. 0 stands for no limit
	 * @return
	 */
	public int getClientLimit();
	
	/**
	 * set max client connection number. 0 stands for no limit
	 * @param clientLimit
	 */
	public void setClientLimit(int clientLimit);
	
	/**
	 * 
	 * @return
	 */
	public String getDomain();
	
	/**
	 * 
	 * @param domain
	 */
	public void setDomain(String domain);
	
	/**
	 * 
	 * @return
	 */
	public String getHostName();
	
	/**
	 * 
	 * @param hostName
	 */
	public void setHostName(String hostName);
	
	/**
	 * 
	 * @return
	 */
	public String getRouterIp();
	
	/**
	 * 
	 * @param routerIp
	 */
	public void setRouterIp(String routerIp);
	
	/**
	 * 
	 * @return
	 */
	public int getRouterPort();
	
	/**
	 * 
	 * @param routerPort
	 */
	public void setRouterPort(int routerPort);
	
	/**
	 * 
	 * @return
	 */
	public String getRouterPassword();
	
	/**
	 * 
	 * @param routerPassword
	 */
	public void setRouterPassword(String routerPassword);
	
	/**
	 * start c2s module
	 */
	public void start();
	
	/**
	 * stop c2s module
	 */
	public void stop();
	
	/**
	 *  exit program
	 */
	public void exit();
	
	/**
	 * 
	 */
	public boolean isStarted();
	
	/**
	 * 
	 * @return
	 */
	public boolean isRouterConnected();
}
