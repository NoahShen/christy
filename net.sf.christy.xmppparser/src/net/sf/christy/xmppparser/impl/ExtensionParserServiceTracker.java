/**
 * 
 */
package net.sf.christy.xmppparser.impl;


import net.sf.christy.xmppparser.ExtensionParser;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author noah
 *
 */
public class ExtensionParserServiceTracker extends ServiceTracker
{

	public ExtensionParserServiceTracker(BundleContext context)
	{
		super(context, ExtensionParser.class.getName(), null);
	}

	/**
	 * 
	 * @param elementName
	 * @param namespace
	 * @return
	 */
	public ExtensionParser getExtensionParser(String elementName, String namespace)
	{
		Object[] services = getServices();
		if (services == null)
		{
			return null;
		}
		
		for (Object obj : services)
		{
			ExtensionParser parser = (ExtensionParser) obj;
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
	public ExtensionParser[] getExtensionParsers()
	{
		return (ExtensionParser[]) getServices();
	}
}
