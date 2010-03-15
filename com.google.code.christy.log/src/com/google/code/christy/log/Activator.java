package com.google.code.christy.log;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.log.impl.Slf4jLogger;

public class Activator implements BundleActivator
{

	private ServiceRegistration loggerRegisterService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		Slf4jLogger logger = new Slf4jLogger();
		loggerRegisterService = context.registerService(Logger.class.getName(), logger, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (loggerRegisterService != null)
		{
			loggerRegisterService.unregister();
			loggerRegisterService = null;
		}
	}

}
