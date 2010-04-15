package com.google.code.christy.shopactivityservice;

import java.util.Hashtable;

import javax.servlet.http.HttpServlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.log.LoggerServiceTracker;

public class Activator implements BundleActivator
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	private LoggerServiceTracker loggerServiceTracker;
	private ServiceRegistration shopServletRegistration;

	public void start(BundleContext context) throws Exception
	{
		loggerServiceTracker = new LoggerServiceTracker(context);
		loggerServiceTracker.open();
		
		ShopServlet shopServlet = new ShopServlet(loggerServiceTracker);
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put("contextPath", "/shop");
		properties.put("pathSpec", "/");
		shopServletRegistration = context.registerService(HttpServlet.class.getName(), shopServlet, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (loggerServiceTracker != null)
		{
			loggerServiceTracker.close();
			loggerServiceTracker = null;
		}
		
		if (shopServletRegistration != null)
		{
			shopServletRegistration.unregister();
			shopServletRegistration = null;
		}
	}

}
