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
public class UserPrivacyListDbHelperTracker extends ServiceTracker
{

	public UserPrivacyListDbHelperTracker(BundleContext context)
	{
		super(context, UserPrivacyListDbHelper.class.getName(), null);
	}

	public UserPrivacyListDbHelper getUserPrivacyListDbHelper()
	{
		return (UserPrivacyListDbHelper) getService();
	}
}
