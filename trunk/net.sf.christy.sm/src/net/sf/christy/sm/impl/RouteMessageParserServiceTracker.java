package net.sf.christy.sm.impl;

import net.sf.christy.routemessageparser.RouteMessageParser;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

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
