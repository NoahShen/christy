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
	 * @param resouce
	 * @return
	 */
	public BindTask bindResouce(String jidNode, String resouce);
	
	/**
	 * 
	 * @param jidNode
	 * @param resouce
	 * @param listener
	 * @return
	 */
	public BindTask bindResouce(String jidNode, String resouce, BindTaskListener listener);
	
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
