package com.google.code.christy.module.pubsub.impl;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.module.pubsub.AccessModel;

public class AccessModelTracker extends ServiceTracker
{

	public AccessModelTracker(BundleContext context)
	{
		super(context, AccessModel.class.getName(), null);
	}

	public AccessModel getSubscribeModel(String name)
	{
		Object[] services = getServices();
		if (services == null || services.length == 0)
		{
			return null;
		}
		
		for (Object service : services)
		{
			AccessModel model = (AccessModel) service;
			if (model.getName().equals(name))
			{
				return model;
			}
		}
		return null;
	}
}
