/**
 * 
 */
package net.sf.christy.c2s;

import net.sf.christy.util.Propertied;

/**
 * @author noah
 *
 */
public interface C2SManager extends Propertied
{
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
	 * @param maxClientLimit
	 */
	public void setClientLimit(int maxClientLimit);
	
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
	public String getRouterIP();
	
	/**
	 * 
	 * @param routerIP
	 */
	public void setRouterIP(String routerIP);
	
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
}
