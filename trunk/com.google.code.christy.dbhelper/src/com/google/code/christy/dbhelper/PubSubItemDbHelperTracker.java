package com.google.code.christy.dbhelper;


import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class PubSubItemDbHelperTracker extends ServiceTracker
{

	public PubSubItemDbHelperTracker(BundleContext context)
	{
		super(context, PubSubItemDbHelper.class.getName(), null);
	}

	public PubSubItemDbHelper getPubSubItemDbHelper()
	{
		return (PubSubItemDbHelper) getService();
	}
}
