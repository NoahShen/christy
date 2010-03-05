package com.google.code.christy.c2s.webc2s;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.c2s.C2SManager;
import com.google.code.christy.c2s.ChristyStreamFeature;
import com.google.code.christy.c2s.UserAuthenticator;

public class Activator implements BundleActivator
{

	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;

	private XmppParserServiceTracker xmppParserServiceTracker;

	private ServiceRegistration c2sManagerRegistration;

	private ChristyStreamFeatureServiceTracker streamFeatureStracker;

	private UserAuthenticatorTracker userAuthenticatorTracker;

	private ServiceRegistration resourceBindFeatureRegistration;

	private ServiceRegistration sessionFeatureRegistration;

	private ServiceRegistration plainUserAuthenticatorRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		
		//---------streamFeature

		ChristyStreamFeature resourceBindFeature = 
			new ChristyStreamFeature("bind", "urn:ietf:params:xml:ns:xmpp-bind", ChristyStreamFeature.SupportedType.afterAuth);
		resourceBindFeatureRegistration = context.registerService(ChristyStreamFeature.class.getName(), resourceBindFeature, null);

		ChristyStreamFeature sessionFeature = 
			new ChristyStreamFeature("session", "urn:ietf:params:xml:ns:xmpp-session", ChristyStreamFeature.SupportedType.afterAuth);
		sessionFeatureRegistration = context.registerService(ChristyStreamFeature.class.getName(), sessionFeature, null);
		
		
		streamFeatureStracker = new ChristyStreamFeatureServiceTracker(context);
		streamFeatureStracker.open();
		//---------streamFeature
		
		routeMessageParserServiceTracker = new RouteMessageParserServiceTracker(context);
		routeMessageParserServiceTracker.open();

		xmppParserServiceTracker = new XmppParserServiceTracker(context);
		xmppParserServiceTracker.open();

		streamFeatureStracker = new ChristyStreamFeatureServiceTracker(context);
		streamFeatureStracker.open();
		//---------streamFeature
		
		//authenticator
		PlainUserAuthenticatorImpl plainUserAuthenticator = new PlainUserAuthenticatorImpl();
		plainUserAuthenticatorRegistration = context.registerService(UserAuthenticator.class.getName(), plainUserAuthenticator, null);
		
		userAuthenticatorTracker = new UserAuthenticatorTracker(context);
		userAuthenticatorTracker.open();
		//authenticator
		
		WebC2SManager c2sManager = new WebC2SManager(routeMessageParserServiceTracker, 
								xmppParserServiceTracker,
								streamFeatureStracker,
								userAuthenticatorTracker);

		c2sManagerRegistration = context.registerService(C2SManager.class.getName(), c2sManager, null);

		
		// TODO
		
		c2sManager.setName("c2s_web1");
		c2sManager.setDomain("example.com");
		c2sManager.setRouterIp("localhost");
		c2sManager.setRouterPassword("md5password");
		
		String appPath = System.getProperty("appPath");
		
		c2sManager.setResourceBase(appPath + "/xmppWebClient/");
		
		c2sManager.start();
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
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
		
		if (streamFeatureStracker != null)
		{
			streamFeatureStracker.close();
			streamFeatureStracker = null;
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
		
		if (plainUserAuthenticatorRegistration != null)
		{
			plainUserAuthenticatorRegistration.unregister();
			plainUserAuthenticatorRegistration = null;
		}
	}

}
