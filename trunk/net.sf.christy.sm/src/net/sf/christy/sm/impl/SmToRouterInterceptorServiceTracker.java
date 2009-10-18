/**
 * 
 */
package net.sf.christy.sm.impl;


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

	public boolean fireSmMessageReceived(String routeXml)
	{
		Object[] services = getServices();
		if (services != null)
		{
			for (Object service : services)
			{
				SmToRouterInterceptor interceptor = (SmToRouterInterceptor) service;
				if (interceptor.smMessageReceived(routeXml))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean fireSmMessageSent(String routeXml)
	{
		Object[] services = getServices();
		if (services != null)
		{
			for (Object service : services)
			{
				SmToRouterInterceptor interceptor = (SmToRouterInterceptor) service;
				if (interceptor.smMessageSent(routeXml))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
