/**
 * 
 */
package net.sf.christy.sm.impl;


import net.sf.christy.routemessage.RouteMessage;
import net.sf.christy.sm.SmManager;
import net.sf.christy.sm.SmToRouterInterceptor;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

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

	public boolean fireSmMessageReceived(RouteMessage routeMessage, SmManager smManager)
	{
		Object[] services = getServices();
		if (services != null)
		{
			for (Object service : services)
			{
				SmToRouterInterceptor interceptor = (SmToRouterInterceptor) service;
				if (interceptor.smMessageReceived(routeMessage, smManager))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean fireSmMessageSent(RouteMessage routeMessage, SmManager smManager)
	{
		Object[] services = getServices();
		if (services != null)
		{
			for (Object service : services)
			{
				SmToRouterInterceptor interceptor = (SmToRouterInterceptor) service;
				if (interceptor.smMessageSent(routeMessage, smManager))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
