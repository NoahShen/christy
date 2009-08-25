/**
 * 
 */
package net.sf.christy.c2s.impl;

import javax.net.ssl.SSLContext;

import org.osgi.framework.BundleContext;
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
		Object obj = getService();
		return (SSLContext) obj;
	}
}
