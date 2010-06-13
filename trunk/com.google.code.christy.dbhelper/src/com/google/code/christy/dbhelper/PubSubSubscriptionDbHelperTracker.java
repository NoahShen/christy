package com.google.code.christy.dbhelper;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class PubSubSubscriptionDbHelperTracker extends ServiceTracker
{

	public PubSubSubscriptionDbHelperTracker(BundleContext context)
	{
		super(context, PubSubSubscriptionDbHelper.class.getName(), null);
	}
	
	public PubSubSubscriptionDbHelper getPubSubSubscriptionDbHelper()
	{
		return (PubSubSubscriptionDbHelper) getService();
	}

}
