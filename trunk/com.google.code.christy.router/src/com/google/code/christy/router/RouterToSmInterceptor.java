/**
 * 
 */
package com.google.code.christy.router;

import com.google.code.christy.routemessage.RouteMessage;

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
