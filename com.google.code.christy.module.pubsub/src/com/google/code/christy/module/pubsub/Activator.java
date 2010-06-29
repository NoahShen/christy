package com.google.code.christy.module.pubsub;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

	private ServiceRegistration openAccessModelRegistration;
	private ServiceRegistration openPublisherModelRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{		
		OpenAccessModel openAccessModel = new OpenAccessModel();
		openAccessModelRegistration = context.registerService(AccessModel.class.getName(), openAccessModel, null);
		
		OpenPublisherModel openPublisherModel = new OpenPublisherModel();
		openPublisherModelRegistration = context.registerService(PublisherModel.class.getName(), openPublisherModel, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (openAccessModelRegistration != null)
		{
			openAccessModelRegistration.unregister();
			openAccessModelRegistration = null;
		}
		
		if (openPublisherModelRegistration != null)
		{
			openPublisherModelRegistration.unregister();
			openPublisherModelRegistration = null;
		}
	}

}
