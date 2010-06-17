package com.google.code.christy.module.pubsub.impl;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.module.pubsub.SubscribeModel;

public class SubscribeModelTracker extends ServiceTracker
{

	public SubscribeModelTracker(BundleContext context)
	{
		super(context, SubscribeModel.class.getName(), null);
	}

	public SubscribeModel getSubscribeModel(String name)
	{
		Object[] services = getServices();
		if (services == null || services.length == 0)
		{
			return null;
		}
		
		for (Object service : services)
		{
			SubscribeModel model = (SubscribeModel) service;
			if (model.getName().equals(name))
			{
				return model;
			}
		}
		return null;
	}
}
