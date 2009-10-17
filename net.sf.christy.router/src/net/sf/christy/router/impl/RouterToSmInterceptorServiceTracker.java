/**
 * 
 */
package net.sf.christy.router.impl;

import net.sf.christy.router.RouterToSmInterceptor;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

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

	public boolean fireRouteMessageReceived(String routeXml)
	{
		Object[] services = getServices();
		if (services != null)
		{
			for (Object service : services)
			{
				RouterToSmInterceptor interceptor = (RouterToSmInterceptor) service;
				if (interceptor.routeMessageReceived(routeXml))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean fireRouteMessageSent(String routeXml)
	{
		Object[] services = getServices();
		if (services != null)
		{
			for (Object service : services)
			{
				RouterToSmInterceptor interceptor = (RouterToSmInterceptor) service;
				if (interceptor.routeMessageSent(routeXml))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
