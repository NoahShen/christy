/**
 * 
 */
package net.sf.christy.sm;

import net.sf.christy.routemessage.RouteMessage;
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
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public OnlineUser getOnlineUser(String node);
	
	/**
	 * 
	 * @param node
	 * @param resource
	 * @return
	 */
	public UserResource getUserResource(String node, String resource);
	
	/**
	 * 
	 * @return
	 */
	public int getNumberOfOnlineUsers();
	
	/**
	 * 
	 * @param onlineUsersLimit
	 */
	public void setOnlineUsersLimit(int onlineUsersLimit);
	
	/**
	 * 
	 * @return
	 */
	public int getOnlineUsersLimit();
	
	/**
	 * 
	 * @param resourceLimitPerUser
	 */
	public void setResourceLimitPerUser(int resourceLimitPerUser);
	
	/**
	 * 
	 * @return
	 */
	public int getResourceLimitPerUser();
	
	/**
	 * 
	 * @param routeMessage
	 */
	public void sendToRouter(RouteMessage routeMessage);

	/**
	 * 
	 * @param onlineUser
	 */
	public void removeOnlineUser(OnlineUser onlineUser);

	/**
	 * 
	 * @param node
	 */
	public void removeOnlineUser(String node);

	/**
	 * 
	 * @param userNode
	 * @param resource
	 * @param relatedC2s
	 * @param streamId
	 * @return
	 */
	public UserResource createUserResource(String userNode, String resource, String relatedC2s, String streamId);
	
	/**
	 * 
	 * @param userNode
	 * @param resource
	 * @return
	 */
	public boolean containUserResource(String userNode, String resource);
	
	/**
	 * 
	 * @param node
	 * @param resource
	 */
	public void removeUserResource(String node, String resource);
}
