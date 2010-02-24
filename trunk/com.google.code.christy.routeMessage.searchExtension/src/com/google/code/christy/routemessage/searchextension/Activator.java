package com.google.code.christy.routemessage.searchextension;



import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.routemessage.searchextension.parser.SearchCompletedExtensionParser;
import com.google.code.christy.routemessage.searchextension.parser.SearchRouteExtensionParser;
import com.google.code.christy.routemessage.searchextension.parser.XmppParserServiceTracker;
import com.google.code.christy.routemessageparser.RouteExtensionParser;

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
