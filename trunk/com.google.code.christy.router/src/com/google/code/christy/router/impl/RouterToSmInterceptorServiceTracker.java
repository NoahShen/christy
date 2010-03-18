/**
 * 
 */
package com.google.code.christy.router.impl;


import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.router.RouterManager;
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

	public boolean fireRouteMessageReceived(RouterManager routerManager, RouteMessage routeMessage, SmSession smSession)
	{
		Object[] services = getServices();
		if (services != null)
		{
			for (Object service : services)
			{
				RouterToSmInterceptor interceptor = (RouterToSmInterceptor) service;
				if (interceptor.routeMessageReceived(routerManager, routeMessage, smSession))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean fireRouteMessageSent(RouterManager routerManager, RouteMessage routeMessage, SmSession smSession)
	{
		Object[] services = getServices();
		if (services != null)
		{
			for (Object service : services)
			{
				RouterToSmInterceptor interceptor = (RouterToSmInterceptor) service;
				if (interceptor.routeMessageSent(routerManager, routeMessage, smSession))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
