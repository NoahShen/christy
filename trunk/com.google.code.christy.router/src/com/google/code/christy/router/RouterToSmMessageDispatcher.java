/**
 * 
 */
package com.google.code.christy.router;


import com.google.code.christy.routemessage.RouteMessage;

/**
 * @author noah
 *
 */
public interface RouterToSmMessageDispatcher
{
	/**
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 
	 */
	public void sendMessage(RouteMessage routeMessage);
	
	/**
	 * 
	 * @param smSession
	 */
	public void smSessionAdded(SmSession smSession);
	
	/**
	 * 
	 * @param smSession
	 */
	public void smSessionRemoved(SmSession smSession);

}
