/**
 * 
 */
package net.sf.christy.sm;

/**
 * @author noah
 *
 */
public interface SmToRouterInterceptor
{
	/**
	 * 
	 * @param routeXml
	 * @return
	 */
	public boolean smMessageReceived(String routeXml);
	
	/**
	 * 
	 * @param routeXml
	 * @return
	 */
	public boolean smMessageSent(String routeXml);
}
