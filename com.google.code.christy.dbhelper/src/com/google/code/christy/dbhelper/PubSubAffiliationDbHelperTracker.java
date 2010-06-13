package com.google.code.christy.dbhelper;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class PubSubAffiliationDbHelperTracker extends ServiceTracker
{

	public PubSubAffiliationDbHelperTracker(BundleContext context)
	{
		super(context, PubSubAffiliationDbHelper.class.getName(), null);
	}
	
	public PubSubAffiliationDbHelper getPubSubAffiliationDbHelper()
	{
		return (PubSubAffiliationDbHelper) getService();
	}

}
