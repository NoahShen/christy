package com.google.code.christy.shopactivityservice;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.c2s.C2SManager;


public class C2SManagerTracker extends ServiceTracker
{

	public C2SManagerTracker(BundleContext context)
	{
		super(context, C2SManager.class.getName(), null);
	}
	
	public boolean containStreamId(String streamId)
	{
		Object service = getService();
		if (service != null)
		{
			C2SManager manager = (C2SManager) service;
			return manager.containStreamId(streamId);
		}
		
		return false;
	}

}
