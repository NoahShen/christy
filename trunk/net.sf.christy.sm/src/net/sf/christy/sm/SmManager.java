/**
 * 
 */
package net.sf.christy.sm;

import net.sf.christy.util.Propertied;

/**
 * @author noah
 *
 */
public interface SmManager extends Propertied
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
	public String getRouterIp();
	
	/**
	 * 
	 * @param routerIP
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
