package com.google.code.christy.router.impl;


import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.router.RouterToSmMessageDispatcher;

public class RouterToSmMessageDispatcherTracker extends ServiceTracker
{

	public RouterToSmMessageDispatcherTracker(BundleContext context)
	{
		super(context, RouterToSmMessageDispatcher.class.getName(), null);
	}

	public RouterToSmMessageDispatcher getDispatcher()
	{
		return (RouterToSmMessageDispatcher) getService();
	}
}
