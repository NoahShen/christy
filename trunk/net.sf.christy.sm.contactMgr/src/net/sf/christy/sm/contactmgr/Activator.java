package net.sf.christy.sm.contactmgr;

import net.sf.christy.sm.PacketHandler;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

	private ServiceRegistration contactHandlerRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		ContactHandler contactHandler = new ContactHandler();
		contactHandlerRegistration = context.registerService(PacketHandler.class.getName(), contactHandler, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (contactHandlerRegistration != null)
		{
			contactHandlerRegistration.unregister();
			contactHandlerRegistration = null;
		}
	}

}
