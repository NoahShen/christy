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
	 * @return
	 */
	public BindTask bindResouce(String jidNode, String xml, Map<String, Object> properties);
	
	/**
	 * 
	 * @param jidNode
	 * @param xml
	 * @param properties
	 * @param listener
	 * @return
	 */
	public BindTask bindResouce(String jidNode, String xml, Map<String, Object> properties, BindTaskListener listener);
	
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
