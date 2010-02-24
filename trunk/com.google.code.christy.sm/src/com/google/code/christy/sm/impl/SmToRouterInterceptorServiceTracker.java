/**
 * 
 */
package com.google.code.christy.sm.impl;



import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.sm.OnlineUser;
import com.google.code.christy.sm.SmManager;
import com.google.code.christy.sm.SmToRouterInterceptor;

/**
 * @author noah
 *
 */
public class SmToRouterInterceptorServiceTracker extends ServiceTracker
{

	public SmToRouterInterceptorServiceTracker(BundleContext context)
	{
		super(context, SmToRouterInterceptor.class.getName(), null);
	}

	public boolean fireSmMessageReceived(RouteMessage routeMessage, SmManager smManager, OnlineUser onlineUser)
	{
		Object[] services = getServices();
		if (services != null)
		{
			for (Object service : services)
			{
				SmToRouterInterceptor interceptor = (SmToRouterInterceptor) service;
				if (interceptor.smMessageReceived(routeMessage, smManager, onlineUser))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean fireSmMessageSent(RouteMessage routeMessage, SmManager smManager, OnlineUser onlineUser)
	{
		Object[] services = getServices();
		if (services != null)
		{
			for (Object service : services)
			{
				SmToRouterInterceptor interceptor = (SmToRouterInterceptor) service;
				if (interceptor.smMessageSent(routeMessage, smManager, onlineUser))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
