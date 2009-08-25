package net.sf.christy.c2s;

import java.util.Hashtable;

import javax.net.ssl.SSLContext;

import net.sf.christy.c2s.impl.C2SManagerImpl;
import net.sf.christy.c2s.impl.ChristyStreamFeatureServiceTracker;
import net.sf.christy.c2s.impl.TlsContextServiceTracker;
import net.sf.christy.c2s.impl.tls.BogusSSLContextFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

	private ChristyStreamFeatureServiceTracker streamFeatureStracker;
	private ServiceRegistration tlsFeatureRegistration;
	private TlsContextServiceTracker tlsContextServiceTracker;
	private ServiceRegistration sslContextRegistration;

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

		SSLContext sslContext = BogusSSLContextFactory.getInstance(true);
		Hashtable<String, String> prop = new Hashtable<String, String>();
		prop.put("tlsContext", "true");
		
		sslContextRegistration = context.registerService(SSLContext.class.getName(), sslContext, prop);
		
		tlsContextServiceTracker = new TlsContextServiceTracker(context);
		tlsContextServiceTracker.open();
		
		C2SManagerImpl c2sManager = new C2SManagerImpl(streamFeatureStracker, tlsContextServiceTracker);
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
		
		if (tlsContextServiceTracker != null)
		{
			tlsContextServiceTracker.close();
			tlsContextServiceTracker = null;
		}
		
		if (sslContextRegistration != null)
		{
			sslContextRegistration.unregister();
			sslContextRegistration = null;
		}
	}

}
