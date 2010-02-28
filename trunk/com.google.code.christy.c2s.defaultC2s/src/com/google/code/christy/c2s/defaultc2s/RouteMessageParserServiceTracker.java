package com.google.code.christy.c2s.defaultc2s;


import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.routemessageparser.RouteMessageParser;

public class RouteMessageParserServiceTracker extends ServiceTracker
{

	public RouteMessageParserServiceTracker(BundleContext context)
	{
		super(context, RouteMessageParser.class.getName(), null);
	}

	public RouteMessageParser getRouteMessageParser()
	{
		Object obj = getService();
		if (obj == null)
		{
			return null;
		}
		
		return (RouteMessageParser) obj;
	}
}
