package com.google.code.christy.enterprise;

import org.apache.commons.configuration.XMLConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.code.christy.log.LoggerServiceTracker;

public class Activator implements BundleActivator
{

	private LoggerServiceTracker loggerServiceTracker;
	private EnterpriseServer enterpriseServer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		loggerServiceTracker = new LoggerServiceTracker(context);
		loggerServiceTracker.open();
		
		
		String appPath = System.getProperty("appPath");
		XMLConfiguration config = new XMLConfiguration(appPath + "/enterpriseserverconfig.xml");
		
		int port = config.getInt("web-client-port", 10007);
		
		String contextPath = config.getString("context-path", "/");
		
		String resourceBase = config.getString("resource-base", "/page");
		
		enterpriseServer = new EnterpriseServer(loggerServiceTracker);
		
		enterpriseServer.setPort(port);
		enterpriseServer.setContextPath(contextPath);
		enterpriseServer.setResourceBase(appPath + "/" + resourceBase);
		
		enterpriseServer.start();
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
		
		if (enterpriseServer != null)
		{
			enterpriseServer.stop();
			enterpriseServer = null;
		}
	}

}
