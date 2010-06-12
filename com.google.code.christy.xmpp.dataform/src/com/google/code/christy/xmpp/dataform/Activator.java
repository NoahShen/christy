package com.google.code.christy.xmpp.dataform;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.xmpp.dataform.parser.JabberDataFormExtensionParser;
import com.google.code.christy.xmppparser.ExtensionParser;

public class Activator implements BundleActivator
{
	private ServiceRegistration dataFormParserRegistration;
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		JabberDataFormExtensionParser dataFormParser = new JabberDataFormExtensionParser();
		dataFormParserRegistration = context.registerService(ExtensionParser.class.getName(), dataFormParser, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (dataFormParserRegistration != null)
		{
			dataFormParserRegistration.unregister();
			dataFormParserRegistration = null;
		}

	}

}
