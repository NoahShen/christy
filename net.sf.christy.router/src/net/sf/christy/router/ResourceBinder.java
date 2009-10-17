/**
 * 
 */
package net.sf.christy.router;

import java.util.Map;

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
	 * @param jidNode
	 * @param xml
	 * @param properties
	 */
	public void handleRequest(String jidNode, String xml, Map<String, Object> properties);
	
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
