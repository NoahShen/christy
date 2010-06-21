package com.google.code.christy.module.pubsub.impl;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.module.pubsub.PublisherModel;

public class PublisherModelTracker extends ServiceTracker
{

	public PublisherModelTracker(BundleContext context)
	{
		super(context, PublisherModel.class.getName(), null);
	}

	public PublisherModel getPublisherModel(String name)
	{
		Object[] services = getServices();
		if (services == null || services.length == 0)
		{
			return null;
		}
		
		for (Object service : services)
		{
			PublisherModel model = (PublisherModel) service;
			if (model.getName().equals(name))
			{
				return model;
			}
		}
		return null;
	}
}
