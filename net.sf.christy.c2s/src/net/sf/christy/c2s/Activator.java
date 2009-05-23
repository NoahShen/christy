package net.sf.christy.c2s;

import net.sf.christy.c2s.impl.C2SManagerImpl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		C2SManagerImpl c2sManager = new C2SManagerImpl();
		c2sManager.setName("c2s_1");
		c2sManager.setDomain("example.com");
		c2sManager.setRouterIp("localhost");
		c2sManager.setRouterPassword("md5password");
		c2sManager.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
	}

}
