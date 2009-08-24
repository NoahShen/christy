package net.sf.christy.c2s;

import net.sf.christy.c2s.impl.C2SManagerImpl;
import net.sf.christy.c2s.impl.ChristyStreamFeatureServiceTracker;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

	private ChristyStreamFeatureServiceTracker streamFeatureStracker;
	private ServiceRegistration tlsFeatureRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		
		ChristyStreamFeature tlsFeature = 
			new ChristyStreamFeature("starttls", "urn:ietf:params:xml:ns:xmpp-tls", ChristyStreamFeature.SupportedType.afterConnected);
		tlsFeature.setRequired(true);
		tlsFeatureRegistration = context.registerService(ChristyStreamFeature.class.getName(), tlsFeature, null);
		
		streamFeatureStracker = new ChristyStreamFeatureServiceTracker(context);
		streamFeatureStracker.open();

		C2SManagerImpl c2sManager = new C2SManagerImpl(streamFeatureStracker);
		c2sManager.setName("c2s_1");
		c2sManager.setDomain("example.com");
		c2sManager.setRouterIp("localhost");
		c2sManager.setRouterPassword("md5password");
		c2sManager.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (streamFeatureStracker != null)
		{
			streamFeatureStracker.close();
			streamFeatureStracker = null;
		}
		
		if (tlsFeatureRegistration != null)
		{
			tlsFeatureRegistration.unregister();
			tlsFeatureRegistration = null;
		}
	}

}
