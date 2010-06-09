package com.google.code.christy;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class ChristyTracker extends ServiceTracker
{

	public ChristyTracker(BundleContext context)
	{
		super(context, Christy.class.getName(), null);
	}

}
