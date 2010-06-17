package com.google.code.christy.dbhelper;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class PubSubNodeConfigDbHelperTracker extends ServiceTracker
{

	public PubSubNodeConfigDbHelperTracker(BundleContext context)
	{
		super(context, PubSubNodeConfigDbHelper.class.getName(), null);
	}

	public PubSubNodeConfigDbHelper getPubSubNodeConfigDbHelper()
	{
		return (PubSubNodeConfigDbHelper) getService();
	}
}
