package com.google.code.christy.xmpp.privatexml;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.xmpp.privatexml.parser.PrivateXmlExtensionParser;
import com.google.code.christy.xmppparser.ExtensionParser;

public class Activator implements BundleActivator
{
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{

		if (privateXmlExtensionParserRegistration != null)
		{
			privateXmlExtensionParserRegistration.unregister();
			privateXmlExtensionParserRegistration = null;
		}
	}

}
