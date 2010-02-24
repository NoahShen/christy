/**
 * 
 */
package com.google.code.christy.xmppparser.impl;



import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.xmppparser.ExtensionParser;

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
