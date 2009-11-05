/**
 * 
 */
package net.sf.christy.router;


import net.sf.christy.routemessage.RouteMessage;

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
