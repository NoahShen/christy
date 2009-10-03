/**
 * 
 */
package net.sf.christy.router;

/**
 * @author noah
 *
 */
public interface BindTaskListener
{
	/**
	 * 
	 * @param binder
	 * @param task
	 * @param smSession
	 */
	public void resourceBindSuccess(ResourceBinder binder, BindTask task, SmSession smSession);

	/**
	 * 
	 * @param binder
	 * @param task
	 */
	public void resourceBindFailure(ResourceBinder binder, BindTask task);
	
	/**
	 * 
	 * @param binder
	 * @param task
	 * @param newSmSession
	 */
	public void requestRedirect(ResourceBinder binder, BindTask task, SmSession newSmSession); 
}
