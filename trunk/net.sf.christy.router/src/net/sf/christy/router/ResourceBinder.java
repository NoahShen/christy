/**
 * 
 */
package net.sf.christy.router;

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
	 * @return
	 */
	public BindTask bindResouce(String jidNode, String xml);
	
	/**
	 * 
	 * @param jidNode
	 * @param xml
	 * @param listener
	 * @return
	 */
	public BindTask bindResouce(String jidNode, String xml, BindTaskListener listener);
	
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
