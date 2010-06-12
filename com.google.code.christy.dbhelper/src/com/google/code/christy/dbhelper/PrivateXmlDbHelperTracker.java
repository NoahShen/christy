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
public class PrivateXmlDbHelperTracker extends ServiceTracker
{

	public PrivateXmlDbHelperTracker(BundleContext context)
	{
		super(context, PrivateXmlDbHelper.class.getName(), null);
	}

	public PrivateXmlDbHelper getPrivateXmlDbHelper()
	{
		return (PrivateXmlDbHelper) getService();
	}
}
