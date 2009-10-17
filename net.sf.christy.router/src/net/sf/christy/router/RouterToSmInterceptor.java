/**
 * 
 */
package net.sf.christy.router;

/**
 * @author noah
 *
 */
public interface RouterToSmInterceptor
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
	public boolean routeMessageSent(String routeXml);
}
