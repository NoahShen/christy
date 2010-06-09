package com.google.code.christy;

import org.apache.commons.configuration.XMLConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.impl.ChristyImpl;

public class Activator implements BundleActivator
{

	private ServiceRegistration christyRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		String appPath = System.getProperty("appPath");
		String configName = System.getProperty("configName");
		XMLConfiguration config = new XMLConfiguration(appPath + "/" + configName);
		ChristyImpl christy = new ChristyImpl();
		christy.setProperty("config", config);
		christyRegistration = context.registerService(Christy.class.getName(), christy, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (christyRegistration != null)
		{
			christyRegistration.unregister();
			christyRegistration = null;
		}
	}

}
