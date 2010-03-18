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
	 * @param routerManager
	 * @param routeMessage
	 * @param smSession
	 * @return
	 */
	public boolean routeMessageReceived(RouterManager routerManager, RouteMessage routeMessage, SmSession smSession);
	
	/**
	 * 
	 * @param routerManager
	 * @param routeMessagel
	 * @param smSession
	 * @return
	 */
	public boolean routeMessageSent(RouterManager routerManager, RouteMessage routeMessagel, SmSession smSession);
}
