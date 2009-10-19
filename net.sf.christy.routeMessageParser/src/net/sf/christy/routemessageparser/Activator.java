package net.sf.christy.routemessageparser;

import net.sf.christy.routemessageparser.impl.RouteExtensionParserServiceTracker;
import net.sf.christy.routemessageparser.impl.RouteMessageParserImpl;
import net.sf.christy.routemessageparser.impl.XmppParserServiceTracker;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

	private ServiceRegistration sr;
	private RouteExtensionParserServiceTracker routeExtensionParserServiceTracker;
	private XmppParserServiceTracker xmppParserServiceTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		routeExtensionParserServiceTracker = new RouteExtensionParserServiceTracker(context);
		routeExtensionParserServiceTracker.open();

		xmppParserServiceTracker = new XmppParserServiceTracker(context);
		xmppParserServiceTracker.open();
		
		RouteMessageParserImpl parser = 
			new RouteMessageParserImpl(routeExtensionParserServiceTracker, xmppParserServiceTracker);
		sr = context.registerService(RouteMessageParser.class.getName(), parser, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (sr != null)
		{
			sr.unregister();
			sr = null;
		}
		
		if (routeExtensionParserServiceTracker != null)
		{
			routeExtensionParserServiceTracker.close();
			routeExtensionParserServiceTracker = null;
		}
		
		if (xmppParserServiceTracker != null)
		{
			xmppParserServiceTracker.close();
			xmppParserServiceTracker = null;
		}
	}

}
