/**
 * 
 */
package net.sf.christy.router;


import net.sf.christy.routemessage.RouteMessage;

/**
 * @author noah
 *
 */
public interface ResourceBinder
{
	/**
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 
	 * @param routeMessage
	 * @param smSession
	 */
	public void handleRequest(RouteMessage routeMessage, SmSession smSession);
	
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
