package com.google.code.christy.dbhelper;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class PubSubNodeDbHelperTracker extends ServiceTracker
{

	public PubSubNodeDbHelperTracker(BundleContext context)
	{
		super(context, PubSubNodeDbHelper.class.getName(), null);
	}
	
	public PubSubNodeDbHelper getPubSubNodeDbHelper()
	{
		return (PubSubNodeDbHelper) getService();
	}

}
