package com.google.code.christy.sm.vcard;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.dbhelper.VCardDbHelperTracker;
import com.google.code.christy.sm.SmHandler;
import com.google.code.christy.sm.vcard.parser.VCardExtensionParser;
import com.google.code.christy.sm.vcard.parser.VCardTempUpdateExtensionParser;
import com.google.code.christy.xmppparser.ExtensionParser;

public class Activator implements BundleActivator
{

	private ServiceRegistration vCardHanlderRegistration;
	private ServiceRegistration vCardExtensionParserRegistration;
	private ServiceRegistration vCardTempUpdateExtensionParserRegistration;
	private VCardDbHelperTracker vCardDbHelperTracker;

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
		
		vCardDbHelperTracker = new VCardDbHelperTracker(context);
		vCardDbHelperTracker.open();
		
		VCardHanlder vCardHanlder = new VCardHanlder(vCardDbHelperTracker);
		vCardHanlderRegistration = context.registerService(SmHandler.class.getName(), vCardHanlder, null);
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
		
		if (vCardHanlderRegistration != null)
		{
			vCardHanlderRegistration.unregister();
			vCardHanlderRegistration = null;
		}
		
		if (vCardDbHelperTracker != null)
		{
			vCardDbHelperTracker.close();
			vCardDbHelperTracker = null;
		}
	}

}
