package com.google.code.christy.sm.pep;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.sm.SmHandler;

public class Activator implements BundleActivator
{

	private ServiceRegistration pepHandlerRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		PepHandler pepHandler = new PepHandler();
		pepHandlerRegistration = context.registerService(SmHandler.class.getName(), pepHandler, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (pepHandlerRegistration != null)
		{
			pepHandlerRegistration.unregister();
			pepHandlerRegistration = null;
		}
	}

}
