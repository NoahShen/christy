package net.sf.christy.sm.roster;

import net.sf.christy.sm.PacketHandler;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

	private ServiceRegistration rosterHandlerRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		RosterHandler rosterHandler = new RosterHandler();
		rosterHandlerRegistration = context.registerService(PacketHandler.class.getName(), rosterHandler, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (rosterHandlerRegistration != null)
		{
			rosterHandlerRegistration.unregister();
			rosterHandlerRegistration = null;
		}
	}

}
