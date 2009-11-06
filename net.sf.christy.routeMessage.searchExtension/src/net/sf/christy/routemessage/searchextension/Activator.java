package net.sf.christy.routemessage.searchextension;


import net.sf.christy.routemessage.searchextension.parser.SearchCompletedExtensionParser;
import net.sf.christy.routemessage.searchextension.parser.SearchRouteExtensionParser;
import net.sf.christy.routemessage.searchextension.parser.XmppParserServiceTracker;
import net.sf.christy.routemessageparser.RouteExtensionParser;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {


	private ServiceRegistration searchRouteExtensionParserRegistration;

	private ServiceRegistration searchCompletedExtensionParserRegistration;

	private XmppParserServiceTracker xmppParserServiceTracker;
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		
		xmppParserServiceTracker = new XmppParserServiceTracker(context);
		xmppParserServiceTracker.open();
		
		SearchRouteExtensionParser searchRouteExtensionParser = new SearchRouteExtensionParser(xmppParserServiceTracker);
		searchRouteExtensionParserRegistration = 
			context.registerService(RouteExtensionParser.class.getName(), searchRouteExtensionParser, null);
		
		SearchCompletedExtensionParser searchCompletedExtensionParser = new SearchCompletedExtensionParser();
		searchCompletedExtensionParserRegistration = 
			context.registerService(RouteExtensionParser.class.getName(), searchCompletedExtensionParser, null);

	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (searchRouteExtensionParserRegistration != null)
		{
			searchRouteExtensionParserRegistration.unregister();
			searchRouteExtensionParserRegistration = null;
		}
		
		if (searchCompletedExtensionParserRegistration != null)
		{
			searchCompletedExtensionParserRegistration.unregister();
			searchCompletedExtensionParserRegistration = null;
			
		}
		
		if (xmppParserServiceTracker != null)
		{
			xmppParserServiceTracker.close();
			xmppParserServiceTracker = null;
		}
	}

}
