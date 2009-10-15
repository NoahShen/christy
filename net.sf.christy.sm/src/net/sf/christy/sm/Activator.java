package net.sf.christy.sm;

import net.sf.christy.sm.impl.SmManagerImpl;
import net.sf.christy.sm.impl.XmppParserServiceTracker;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

	private XmppParserServiceTracker xmppParserServiceTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		xmppParserServiceTracker = new XmppParserServiceTracker(context);
		xmppParserServiceTracker.open();
		
		SmManagerImpl smManager = new SmManagerImpl(xmppParserServiceTracker);
		smManager.setName("sm_1");
		smManager.setDomain("example.com");
		smManager.setRouterIp("localhost");
		smManager.setRouterPassword("md5password");
		smManager.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (xmppParserServiceTracker != null)
		{
			xmppParserServiceTracker.close();
			xmppParserServiceTracker = null;
		}
	}

}
