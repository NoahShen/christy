package com.google.code.christy.xmpp.disco;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.xmpp.disco.parser.DiscoInfoExtensionParser;
import com.google.code.christy.xmpp.disco.parser.DiscoItemsExtensionParser;
import com.google.code.christy.xmppparser.ExtensionParser;

public class Activator implements BundleActivator
{
	private ServiceRegistration discoInfoExtensionParserRegistration;

	private ServiceRegistration discoItemsExtensionParserRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		DiscoInfoExtensionParser discoInfoExtensionParser = new DiscoInfoExtensionParser();
		discoInfoExtensionParserRegistration = 
			context.registerService(ExtensionParser.class.getName(), discoInfoExtensionParser, null);
		
		DiscoItemsExtensionParser discoItemsExtensionParser = new DiscoItemsExtensionParser();
		discoItemsExtensionParserRegistration = 
			context.registerService(ExtensionParser.class.getName(), discoItemsExtensionParser, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (discoInfoExtensionParserRegistration != null)
		{
			discoInfoExtensionParserRegistration.unregister();
			discoInfoExtensionParserRegistration = null;
		}
		
		if (discoItemsExtensionParserRegistration != null)
		{
			discoItemsExtensionParserRegistration.unregister();
			discoItemsExtensionParserRegistration = null;
		}
	}

}
