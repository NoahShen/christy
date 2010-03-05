/**
 * 
 */
package com.google.code.christy.routemessage.searchextension.parser;



import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.xmppparser.XmppParser;

/**
 * @author noah
 *
 */
public class XmppParserServiceTracker extends ServiceTracker
{

	public XmppParserServiceTracker(BundleContext context)
	{
		super(context, XmppParser.class.getName(), null);
	}

	public XmppParser getParser()
	{
		return (XmppParser) getService();
	}
}
