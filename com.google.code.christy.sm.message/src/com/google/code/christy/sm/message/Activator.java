package com.google.code.christy.sm.message;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.sm.SmHandler;

public class Activator implements BundleActivator
{

	private ServiceRegistration messageHandlerRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		MessageHandler messageHandler = new MessageHandler();
		messageHandlerRegistration = context.registerService(SmHandler.class.getName(), messageHandler, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (messageHandlerRegistration != null)
		{
			messageHandlerRegistration.unregister();
			messageHandlerRegistration = null;
		}
	}

}
