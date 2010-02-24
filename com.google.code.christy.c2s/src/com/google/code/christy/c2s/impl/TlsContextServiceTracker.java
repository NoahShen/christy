/**
 * 
 */
package com.google.code.christy.c2s.impl;

import javax.net.ssl.SSLContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author noah
 *
 */
public class TlsContextServiceTracker extends ServiceTracker
{

	public TlsContextServiceTracker(BundleContext context)
	{
		super(context, SSLContext.class.getName(), null);
	}

	public SSLContext getTlsContext()
	{
		ServiceReference sr = getServiceReference();
		if (sr != null)
		{
			Object obj = sr.getProperty("tlsContext");
			if ("true".equals(obj))
			{
				return (SSLContext) getService(sr);
			}
		}
		return null;
	}
}
