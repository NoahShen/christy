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
	 * @return
	 */
	public boolean smMessageReceived(RouteMessage routeMessage, SmManager smManager);
	
	/**
	 * 
	 * @param routeMessage
	 * @param smManager
	 * @return
	 */
	public boolean smMessageSent(RouteMessage routeMessage, SmManager smManager);
}
