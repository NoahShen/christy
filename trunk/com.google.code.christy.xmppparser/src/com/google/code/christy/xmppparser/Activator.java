package com.google.code.christy.xmppparser;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.xmppparser.impl.ExtensionParserServiceTracker;
import com.google.code.christy.xmppparser.impl.XMPPParserImpl;

public class Activator implements BundleActivator
{

	private ServiceRegistration sr;
	private ExtensionParserServiceTracker tracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		tracker = new ExtensionParserServiceTracker(context);
		tracker.open();
		XMPPParserImpl parser = new XMPPParserImpl(tracker);
		sr = context.registerService(XmppParser.class.getName(), parser, null);
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
		
		if (tracker != null)
		{
			tracker.close();
			tracker = null;
		}
	}

}
