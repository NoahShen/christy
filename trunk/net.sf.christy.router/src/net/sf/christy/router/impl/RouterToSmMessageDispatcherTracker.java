package net.sf.christy.router.impl;

import net.sf.christy.router.RouterToSmMessageDispatcher;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class RouterToSmMessageDispatcherTracker extends ServiceTracker
{

	public RouterToSmMessageDispatcherTracker(BundleContext context)
	{
		super(context, RouterToSmMessageDispatcher.class.getName(), null);
	}

	public RouterToSmMessageDispatcher getResourceBinder()
	{
		return (RouterToSmMessageDispatcher) getService();
	}
}
