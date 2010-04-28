package com.google.code.christy.xmpp.resultsetmgr;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.xmpp.resultsetmgr.parser.ResultSetExtensionParser;
import com.google.code.christy.xmppparser.ExtensionParser;

public class Activator implements BundleActivator
{

	private ServiceRegistration resultSetExtensionParserRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		ResultSetExtensionParser resultSetExtensionParser = new ResultSetExtensionParser();
		resultSetExtensionParserRegistration = context.registerService(ExtensionParser.class.getName(), resultSetExtensionParser, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (resultSetExtensionParserRegistration != null)
		{
			resultSetExtensionParserRegistration.unregister();
			resultSetExtensionParserRegistration = null;
		}
	}

}
