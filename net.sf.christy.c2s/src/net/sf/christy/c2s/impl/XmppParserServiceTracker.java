/**
 * 
 */
package net.sf.christy.c2s.impl;


import net.sf.christy.xmppparser.XmppParser;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

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
