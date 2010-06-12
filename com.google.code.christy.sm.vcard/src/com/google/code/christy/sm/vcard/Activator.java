package com.google.code.christy.sm.vcard;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.dbhelper.VCardDbHelperTracker;
import com.google.code.christy.sm.SmHandler;

public class Activator implements BundleActivator
{

	private ServiceRegistration vCardHanlderRegistration;

	private VCardDbHelperTracker vCardDbHelperTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		
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
