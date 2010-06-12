package com.google.code.christy.sm.privatexml;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.dbhelper.PrivateXmlDbHelperTracker;
import com.google.code.christy.sm.SmHandler;
import com.google.code.christy.sm.privatexml.parser.PrivateXmlExtensionParser;
import com.google.code.christy.xmppparser.ExtensionParser;

public class Activator implements BundleActivator
{

	private PrivateXmlDbHelperTracker privateXmlDbHelperTracker;
	private ServiceRegistration privateXmlHandlerRegistration;
	private ServiceRegistration privateXmlExtensionParserRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		PrivateXmlExtensionParser privateXmlExtensionParser = new PrivateXmlExtensionParser();
		privateXmlExtensionParserRegistration = context.registerService(ExtensionParser.class.getName(), privateXmlExtensionParser, null);
		
		privateXmlDbHelperTracker = new PrivateXmlDbHelperTracker(context);
		privateXmlDbHelperTracker.open();
		
		PrivateXmlHandler privateXmlHandler = new PrivateXmlHandler(privateXmlDbHelperTracker);
		privateXmlHandlerRegistration = context.registerService(SmHandler.class.getName(), privateXmlHandler, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (privateXmlDbHelperTracker != null)
		{
			privateXmlDbHelperTracker.close();
			privateXmlDbHelperTracker = null;
		}
		
		if (privateXmlHandlerRegistration != null)
		{
			privateXmlHandlerRegistration.unregister();
			privateXmlHandlerRegistration = null;
		}
		
		if (privateXmlExtensionParserRegistration != null)
		{
			privateXmlExtensionParserRegistration.unregister();
			privateXmlExtensionParserRegistration = null;
		}
	}

}
