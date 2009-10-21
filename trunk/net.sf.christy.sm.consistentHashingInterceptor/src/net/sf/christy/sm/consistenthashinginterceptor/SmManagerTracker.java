/**
 * 
 */
package net.sf.christy.sm.consistenthashinginterceptor;

import net.sf.christy.sm.SmManager;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author noah
 *
 */
public class SmManagerTracker extends ServiceTracker
{

	public SmManagerTracker(BundleContext context)
	{
		super(context, SmManager.class.getName(), null);
	}
	
	public SmManager getSmManager()
	{
		return (SmManager) getService();
	}

}
