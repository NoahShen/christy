package com.google.code.christy.c2s.defaultc2s;

import java.util.Hashtable;

import javax.net.ssl.SSLContext;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.c2s.C2SManager;
import com.google.code.christy.c2s.ChristyStreamFeature;
import com.google.code.christy.c2s.UserAuthenticator;
import com.google.code.christy.c2s.defaultc2s.tls.BogusSSLContextFactory;

public class Activator implements BundleActivator
{

	private ChristyStreamFeatureServiceTracker streamFeatureStracker;
	private ServiceRegistration tlsFeatureRegistration;
	private TlsContextServiceTracker tlsContextServiceTracker;
	private ServiceRegistration sslContextRegistration;
	private ServiceRegistration plainUserAuthenticatorRegistration;
	private UserAuthenticatorTracker userAuthenticatorTracker;
	private ServiceRegistration resourceBindFeatureRegistration;
	private ServiceRegistration sessionFeatureRegistration;
	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;
	private XmppParserServiceTracker xmppParserServiceTracker;
	private ServiceRegistration c2sManagerRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		//---------streamFeature
		
		ChristyStreamFeature tlsFeature = 
			new ChristyStreamFeature("starttls", "urn:ietf:params:xml:ns:xmpp-tls", ChristyStreamFeature.SupportedType.afterConnected);
		tlsFeature.setRequired(true);
		tlsFeatureRegistration = context.registerService(ChristyStreamFeature.class.getName(), tlsFeature, null);

		ChristyStreamFeature resourceBindFeature = 
			new ChristyStreamFeature("bind", "urn:ietf:params:xml:ns:xmpp-bind", ChristyStreamFeature.SupportedType.afterAuth);
		resourceBindFeatureRegistration = context.registerService(ChristyStreamFeature.class.getName(), resourceBindFeature, null);

		ChristyStreamFeature sessionFeature = 
			new ChristyStreamFeature("session", "urn:ietf:params:xml:ns:xmpp-session", ChristyStreamFeature.SupportedType.afterAuth);
		sessionFeatureRegistration = context.registerService(ChristyStreamFeature.class.getName(), sessionFeature, null);
		
		
		streamFeatureStracker = new ChristyStreamFeatureServiceTracker(context);
		streamFeatureStracker.open();
		//---------streamFeature
		
		//sslcontext
		SSLContext sslContext = BogusSSLContextFactory.getInstance(true);
		Hashtable<String, String> prop = new Hashtable<String, String>();
		prop.put("tlsContext", "true");
		
		sslContextRegistration = context.registerService(SSLContext.class.getName(), sslContext, prop);
		
		tlsContextServiceTracker = new TlsContextServiceTracker(context);
		tlsContextServiceTracker.open();
		//sslcontext
		
		//authenticator
		PlainUserAuthenticatorImpl plainUserAuthenticator = new PlainUserAuthenticatorImpl();
		plainUserAuthenticatorRegistration = context.registerService(UserAuthenticator.class.getName(), plainUserAuthenticator, null);
		
		userAuthenticatorTracker = new UserAuthenticatorTracker(context);
		userAuthenticatorTracker.open();
		//authenticator
		
		
		routeMessageParserServiceTracker = new RouteMessageParserServiceTracker(context);
		routeMessageParserServiceTracker.open();

		xmppParserServiceTracker = new XmppParserServiceTracker(context);
		xmppParserServiceTracker.open();
		
		C2SManagerImpl c2sManager = new C2SManagerImpl(streamFeatureStracker, 
												tlsContextServiceTracker,
												userAuthenticatorTracker,
												xmppParserServiceTracker,
												routeMessageParserServiceTracker);
		
		c2sManagerRegistration = context.registerService(C2SManager.class.getName(), c2sManager, null);
		
		// TODO
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
		
		if (plainUserAuthenticatorRegistration != null)
		{
			plainUserAuthenticatorRegistration.unregister();
			plainUserAuthenticatorRegistration = null;
		}
		
		if (userAuthenticatorTracker != null)
		{
			userAuthenticatorTracker.close();
			userAuthenticatorTracker = null;
		}
		
		if (resourceBindFeatureRegistration != null)
		{
			resourceBindFeatureRegistration.unregister();
			resourceBindFeatureRegistration = null;
		}
		
		if (sessionFeatureRegistration != null)
		{
			sessionFeatureRegistration.unregister();
			sessionFeatureRegistration = null;
		}
		
		if (routeMessageParserServiceTracker != null)
		{
			routeMessageParserServiceTracker.close();
			routeMessageParserServiceTracker = null;
		}
		
		if (xmppParserServiceTracker != null)
		{
			xmppParserServiceTracker.close();
			xmppParserServiceTracker = null;
		}
		
		if (c2sManagerRegistration != null)
		{
			c2sManagerRegistration.unregister();
			c2sManagerRegistration = null;
		}
		
		
		
	}

}
