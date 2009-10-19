/**
 * 
 */
package net.sf.christy.routemessageparser.impl;


import net.sf.christy.routemessageparser.RouteExtensionParser;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author noah
 *
 */
public class RouteExtensionParserServiceTracker extends ServiceTracker
{

	public RouteExtensionParserServiceTracker(BundleContext context)
	{
		super(context, RouteExtensionParser.class.getName(), null);
	}

	/**
	 * 
	 * @param elementName
	 * @param namespace
	 * @return
	 */
	public RouteExtensionParser getRouteExtensionParser(String elementName, String namespace)
	{
		Object[] services = getServices();
		if (services == null)
		{
			return null;
		}
		
		for (Object obj : services)
		{
			RouteExtensionParser parser = (RouteExtensionParser) obj;
			if (parser.getElementName().equals(elementName) 
					&& parser.getNamespace().equals(namespace))
			{
				return parser;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public RouteExtensionParser[] getRouteExtensionParsers()
	{
		return (RouteExtensionParser[]) getServices();
	}
}
