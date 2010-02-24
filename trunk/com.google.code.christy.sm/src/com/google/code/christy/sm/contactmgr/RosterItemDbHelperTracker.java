/**
 * 
 */
package com.google.code.christy.sm.contactmgr;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Noah
 *
 */
public class RosterItemDbHelperTracker extends ServiceTracker
{

	public RosterItemDbHelperTracker(BundleContext context)
	{
		super(context, RosterItemDbHelper.class.getName(), null);
	}

	public RosterItemDbHelper getRosterItemDbHelper()
	{
		return (RosterItemDbHelper) getService();
	}
}
