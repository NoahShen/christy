/**
 * 
 */
package com.google.code.christy.dbhelper;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Noah
 *
 */
public class OfflineSubscribeMsgDbHelperTracker extends ServiceTracker
{

	public OfflineSubscribeMsgDbHelperTracker(BundleContext context)
	{
		super(context, OfflineSubscribeMsgDbHelper.class.getName(), null);
	}
	
	public OfflineSubscribeMsgDbHelper getOfflineSubscribeMsgDbHelper()
	{
		return (OfflineSubscribeMsgDbHelper) getService();
	}

}
