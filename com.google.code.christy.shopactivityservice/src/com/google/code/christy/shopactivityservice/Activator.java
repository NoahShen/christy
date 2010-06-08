package com.google.code.christy.shopactivityservice;

import java.util.Hashtable;

import javax.servlet.http.HttpServlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.lib.ConnectionPool;
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
	
	private ConnectionPool connPool;
	
	private C2SManagerTracker c2SManagerTracker;

	private CacheServiceTracker cacheServiceTracker;

	public void start(BundleContext context) throws Exception
	{
		loggerServiceTracker = new LoggerServiceTracker(context);
		loggerServiceTracker.open();
		
		c2SManagerTracker = new C2SManagerTracker(context);
		c2SManagerTracker.open();
		
		connPool = new ConnectionPool("com.mysql.jdbc.Driver",
						"jdbc:mysql://localhost/christy?useUnicode=true&characterEncoding=UTF-8",
						"root",
						"123456");
		connPool .createPool();
		
		ShopDbhelper shopDbhelper = new ShopDbhelper(loggerServiceTracker, connPool);
		UserDbhelper userDbhelper = new UserDbhelper(loggerServiceTracker, connPool);
		
		cacheServiceTracker = new CacheServiceTracker(context);
		cacheServiceTracker.open();
		
		ShopServlet shopServlet = new ShopServlet(c2SManagerTracker, loggerServiceTracker, shopDbhelper, userDbhelper, cacheServiceTracker);
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
		
		if (connPool != null)
		{
			connPool.closeConnectionPool();
		}
		
		if (shopServletRegistration != null)
		{
			shopServletRegistration.unregister();
			shopServletRegistration = null;
		}
		
		if (c2SManagerTracker != null)
		{
			c2SManagerTracker.close();
			c2SManagerTracker = null;
		}
		
		if (cacheServiceTracker != null)
		{
			cacheServiceTracker.close();
			cacheServiceTracker = null;
		}
	}

}
