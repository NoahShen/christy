package com.google.code.christy.router;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.code.christy.log.LoggerServiceTracker;
import com.google.code.christy.router.controller.RouterController;
import com.google.code.christy.router.impl.RouteMessageParserServiceTracker;
import com.google.code.christy.router.impl.RouterManagerImpl;
import com.google.code.christy.router.impl.RouterToSmInterceptorServiceTracker;
import com.google.code.christy.router.impl.RouterToSmMessageDispatcherTracker;

public class Activator implements BundleActivator
{


	private RouterToSmMessageDispatcherTracker routerToSmMessageDispatcherTracker;
	private RouterToSmInterceptorServiceTracker routerToSmInterceptorServiceTracker;
	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;
	private LoggerServiceTracker loggerServiceTracker;
	private RouterController routerController;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{

		routerToSmMessageDispatcherTracker = new RouterToSmMessageDispatcherTracker(context);
		routerToSmMessageDispatcherTracker.open();

		routerToSmInterceptorServiceTracker = new RouterToSmInterceptorServiceTracker(context);
		routerToSmInterceptorServiceTracker.open();
		
		routeMessageParserServiceTracker = new RouteMessageParserServiceTracker(context);
		routeMessageParserServiceTracker.open();
		
		loggerServiceTracker = new LoggerServiceTracker(context);
		loggerServiceTracker.open();
		
		RouterManagerImpl rm = new RouterManagerImpl(routerToSmMessageDispatcherTracker, 
											routerToSmInterceptorServiceTracker,
											routeMessageParserServiceTracker,
											loggerServiceTracker);
		
		
//		routerController = new RouterController(rm);
//		routerController.start();
		
		String appPath = System.getProperty("appPath");
		XMLConfiguration config = new XMLConfiguration(appPath + "/routerconfig.xml");
		
		String domain = config.getString("domain", "example.com");
		rm.setDomain(domain);
		
		List<?> smModules = config.configurationsAt("sm-modules.sm-module");
		
		for (Iterator<?> it = smModules.iterator(); it.hasNext();)
		{
			HierarchicalConfiguration sub = (HierarchicalConfiguration) it.next();
			String name = sub.getString("name");
			String password = sub.getString("password");
			rm.registerSmModule(name, password);
		}

		List<?> c2sModules = config.configurationsAt("c2s-modules.c2s-module");
		
		for (Iterator<?> it = c2sModules.iterator(); it.hasNext();)
		{
			HierarchicalConfiguration sub = (HierarchicalConfiguration) it.next();
			String name = sub.getString("name");
			String password = sub.getString("password");
			rm.registerC2sModule(name, password);
		}
		
		int c2sPort = config.getInt("c2s-port", 8787);
		rm.setC2sPort(c2sPort);
		
		int s2sPort = config.getInt("s2s-port", 8788);
		rm.setS2sPort(s2sPort);
		
		int smPort = config.getInt("sm-port", 8789);
		rm.setSmPort(smPort);
		
		int c2sLimit = config.getInt("c2s-limit", 0);
		rm.setC2sLimit(c2sLimit);
		
		int smLimit = config.getInt("sm-limit", 0);
		rm.setSmLimit(smLimit);
		
		rm.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (routerToSmMessageDispatcherTracker != null)
		{
			routerToSmMessageDispatcherTracker.close();
			routerToSmMessageDispatcherTracker = null;
		}
		
		if (routerToSmInterceptorServiceTracker != null)
		{
			routerToSmInterceptorServiceTracker.close();
			routerToSmInterceptorServiceTracker = null;
		}
		
		if (routeMessageParserServiceTracker != null)
		{
			routeMessageParserServiceTracker.close();
			routeMessageParserServiceTracker = null;
		}
		
		if (loggerServiceTracker != null)
		{
			loggerServiceTracker.close();
			loggerServiceTracker = null;
		}
		
		if (routerController != null) 
		{
			routerController.stop();
			routerController = null;
		}
	}

}
