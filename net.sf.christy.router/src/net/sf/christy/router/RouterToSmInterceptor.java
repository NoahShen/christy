/**
 * 
 */
package net.sf.christy.router;

import net.sf.christy.routemessage.RouteMessage;

/**
 * @author noah
 *
 */
public interface RouterToSmInterceptor
{
	/**
	 * 
	 * @param routeMessage
	 * @param smSession
	 * @return
	 */
	public boolean routeMessageReceived(RouteMessage routeMessage, SmSession smSession);
	
	/**
	 * 
	 * @param routeMessagel
	 * @param smSession
	 * @return
	 */
	public boolean routeMessageSent(RouteMessage routeMessagel, SmSession smSession);
}
