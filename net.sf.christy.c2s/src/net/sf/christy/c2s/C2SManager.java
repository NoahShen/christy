/**
 * 
 */
package net.sf.christy.c2s;

/**
 * @author noah
 *
 */
public interface C2SManager
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
	public int getMaxClientConnections();
	
	/**
	 * 
	 * @param maxClientConnections
	 */
	public void setMaxClientConnections(int maxClientConnections);
	
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
	public boolean getAllowRegistration();
	
	/**
	 * 
	 * @param allowRegistration
	 */
	public void setAllowRegistration(boolean allowRegistration);
	
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
}
