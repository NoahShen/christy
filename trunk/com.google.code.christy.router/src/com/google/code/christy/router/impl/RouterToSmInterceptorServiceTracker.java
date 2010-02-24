/**
 * 
 */
package com.google.code.christy.router.impl;


import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.router.RouterToSmInterceptor;
import com.google.code.christy.router.SmSession;

/**
 * @author noah
 *
 */
public class RouterToSmInterceptorServiceTracker extends ServiceTracker
{

	public RouterToSmInterceptorServiceTracker(BundleContext context)
	{
		super(context, RouterToSmInterceptor.class.getName(), null);
	}

	public boolean fireRouteMessageReceived(RouteMessage routeMessage, SmSession smSession)
	{
		Object[] services = getServices();
		if (services != null)
		{
			for (Object service : services)
			{
				RouterToSmInterceptor interceptor = (RouterToSmInterceptor) service;
				if (interceptor.routeMessageReceived(routeMessage, smSession))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean fireRouteMessageSent(RouteMessage routeMessage, SmSession smSession)
	{
		Object[] services = getServices();
		if (services != null)
		{
			for (Object service : services)
			{
				RouterToSmInterceptor interceptor = (RouterToSmInterceptor) service;
				if (interceptor.routeMessageSent(routeMessage, smSession))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
