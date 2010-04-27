/**
 * 
 */
package com.google.code.christy.sm.vcard;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Noah
 *
 */
public class VCardDbHelperTracker extends ServiceTracker
{

	public VCardDbHelperTracker(BundleContext context)
	{
		super(context, VCardDbHelper.class.getName(), null);
	}

	public VCardDbHelper getVCardDbHelper()
	{
		return (VCardDbHelper) getService();
	}
}
