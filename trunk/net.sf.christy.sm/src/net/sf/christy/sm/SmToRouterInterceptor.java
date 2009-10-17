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
	public boolean routeMessageReceived(String routeXml);
	
	/**
	 * 
	 * @param routeXml
	 * @return
	 */
	public boolean routeMessageSended(String routeXml);
}
