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
	 * @return
	 */
	public boolean smMessageReceived(RouteMessage routeMessage);
	
	/**
	 * 
	 * @param routeMessage
	 * @return
	 */
	public boolean smMessageSent(RouteMessage routeMessage);
}
