/**
 * 
 */
package net.sf.christy.sm;

import net.sf.christy.routemessage.RouteMessage;

/**
 * @author noah
 *
 */
public interface SmToRouterInterceptor
{
	/**
	 * 
	 * @param routeMessage
	 * @param smManager
	 * @param onlineUser
	 * @return
	 */
	public boolean smMessageReceived(RouteMessage routeMessage, SmManager smManager, OnlineUser onlineUser);
	
	/**
	 * 
	 * @param routeMessage
	 * @param smManager
	 * @param onlineUser
	 * @return
	 */
	public boolean smMessageSent(RouteMessage routeMessage, SmManager smManager, OnlineUser onlineUser);
}
