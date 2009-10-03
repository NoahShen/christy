package net.sf.christy.router.impl;

import net.sf.christy.router.ResourceBinder;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class ResourceBinderServiceTracker extends ServiceTracker
{

	public ResourceBinderServiceTracker(BundleContext context)
	{
		super(context, ResourceBinder.class.getName(), null);
	}

	public ResourceBinder getResourceBinder()
	{
		return (ResourceBinder) getService();
	}
}
