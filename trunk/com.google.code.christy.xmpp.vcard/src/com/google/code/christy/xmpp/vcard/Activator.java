package com.google.code.christy.xmpp.vcard;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.xmpp.vcard.parser.VCardExtensionParser;
import com.google.code.christy.xmpp.vcard.parser.VCardTempUpdateExtensionParser;
import com.google.code.christy.xmppparser.ExtensionParser;

public class Activator implements BundleActivator
{
	private ServiceRegistration vCardExtensionParserRegistration;
	
	private ServiceRegistration vCardTempUpdateExtensionParserRegistration;
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		VCardExtensionParser vCardExtensionParser = new VCardExtensionParser();
		vCardExtensionParserRegistration = 
			context.registerService(ExtensionParser.class.getName(), vCardExtensionParser, null);
		
		VCardTempUpdateExtensionParser vCardTempUpdateExtensionParser = new VCardTempUpdateExtensionParser();
		vCardTempUpdateExtensionParserRegistration = 
			context.registerService(ExtensionParser.class.getName(), vCardTempUpdateExtensionParser, null);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (vCardExtensionParserRegistration != null)
		{
			vCardExtensionParserRegistration.unregister();
			vCardExtensionParserRegistration = null;
		}
		
		if (vCardTempUpdateExtensionParserRegistration != null)
		{
			vCardTempUpdateExtensionParserRegistration.unregister();
			vCardTempUpdateExtensionParserRegistration = null;
		}
	}

}
