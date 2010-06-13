package com.google.code.christy.xmpp.pubsub;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.xmpp.pubsub.parser.PubSubExtensionParser;
import com.google.code.christy.xmppparser.ExtensionParser;

public class Activator implements BundleActivator
{

	private ServiceRegistration pubSubExtensionParserRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		PubSubExtensionParser pubSubExtensionParser = new PubSubExtensionParser();
		pubSubExtensionParserRegistration = context.registerService(ExtensionParser.class.getName(), pubSubExtensionParser, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (pubSubExtensionParserRegistration != null)
		{
			pubSubExtensionParserRegistration.unregister();
			pubSubExtensionParserRegistration = null;
		}
	}

}
