/**
 * 
 */
package net.sf.christy.router;

/**
 * @author noah
 *
 */
public interface BindTask
{
	/**
	 * 
	 * @return
	 */
	public ResourceBinder getResourceBinder();
	
	/**
	 * 
	 * @return
	 */
	public boolean isCompleted();
	
	/**
	 * 
	 * @return
	 */
	public String getJidNode();
	
	/**
	 * 
	 * @return
	 */
	public String getResource();
	
	/**
	 * 
	 * @return
	 */
	public String getBindedResouce();
	
	/**
	 * 
	 * @param listener
	 */
	public void addBindTaskListener(BindTaskListener listener);
	
	/**
	 * 
	 * @param listener
	 */
	public void removeBindTaskListener(BindTaskListener listener);
	
	/**
	 * 
	 * @return
	 */
	public BindTaskListener[] getBindTaskListeners();
}
