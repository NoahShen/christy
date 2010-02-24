/**
 * 
 */
package com.google.code.christy.sm.user;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Noah
 *
 */
public class UserDbHelperTracker extends ServiceTracker
{

	public UserDbHelperTracker(BundleContext context)
	{
		super(context, UserDbHelper.class.getName(), null);
		// TODO Auto-generated constructor stub
	}

	public UserDbHelper getUserDbHelper()
	{
		return (UserDbHelper) getService();
	}
}
