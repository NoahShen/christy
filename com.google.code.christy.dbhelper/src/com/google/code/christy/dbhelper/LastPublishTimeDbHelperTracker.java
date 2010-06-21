package com.google.code.christy.dbhelper;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class LastPublishTimeDbHelperTracker extends ServiceTracker
{

	public LastPublishTimeDbHelperTracker(BundleContext context)
	{
		super(context, LastPublishTimeDbHelper.class.getName(), null);
	}
	
	public LastPublishTimeDbHelper getLastPublishTimeDbHelper()
	{
		return (LastPublishTimeDbHelper) getService();
	}

}
