package com.google.code.christy.sm.disco;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.sm.SmHandler;
import com.google.code.christy.sm.disco.impl.DiscoInfoFeatureServiceTracker;
import com.google.code.christy.sm.disco.impl.DiscoInfoIdentityServiceTracker;
import com.google.code.christy.sm.disco.impl.DiscoItemServiceTracker;
import com.google.code.christy.sm.disco.impl.DiscoManagerImpl;
import com.google.code.christy.xmpp.disco.DiscoInfoFeature;

public class Activator implements BundleActivator
{

	private DiscoInfoFeatureServiceTracker discoInfoFeatureServiceTracker;

	private DiscoInfoIdentityServiceTracker discoInfoIdentityServiceTracker;

	private DiscoItemServiceTracker discoItemServiceTracker;

	private ServiceRegistration discoManagerRegistration;

	private ServiceRegistration infoFeatureRegistration;

	private ServiceRegistration itemFeatureRegistration;

	public void start(BundleContext context) throws Exception
	{
		DiscoInfoFeature infoFeature = 
			new DiscoInfoFeature(null, "http://jabber.org/protocol/disco#info");
		infoFeatureRegistration = 
			context.registerService(DiscoInfoFeature.class.getName(), infoFeature, null);
		
		DiscoInfoFeature itemFeature = 
			new DiscoInfoFeature(null, "http://jabber.org/protocol/disco#items");
		itemFeatureRegistration = 
			context.registerService(DiscoInfoFeature.class.getName(), itemFeature, null);
		
		
		discoInfoFeatureServiceTracker = new DiscoInfoFeatureServiceTracker(context);
		discoInfoFeatureServiceTracker.open();

		discoInfoIdentityServiceTracker = new DiscoInfoIdentityServiceTracker(context);
		discoInfoIdentityServiceTracker.open();

		discoItemServiceTracker = new DiscoItemServiceTracker(context);
		discoItemServiceTracker.open();
		
		DiscoManagerImpl discoManager = new DiscoManagerImpl(discoInfoFeatureServiceTracker, 
									discoInfoIdentityServiceTracker, 
									discoItemServiceTracker);
		discoManagerRegistration = context.registerService(SmHandler.class.getName(), discoManager, null);
		
		
	}


	public void stop(BundleContext context) throws Exception
	{
		if (discoInfoFeatureServiceTracker != null)
		{
			discoInfoFeatureServiceTracker.close();
			discoInfoFeatureServiceTracker = null;
		}

		if (discoInfoIdentityServiceTracker != null)
		{
			discoInfoIdentityServiceTracker.close();
			discoInfoIdentityServiceTracker = null;
		}

		if (discoItemServiceTracker != null)
		{
			discoItemServiceTracker.close();
			discoItemServiceTracker = null;
		}
		
		if (discoManagerRegistration != null)
		{
			discoManagerRegistration.unregister();
			discoManagerRegistration = null;
		}
		
		if (infoFeatureRegistration != null)
		{
			infoFeatureRegistration.unregister();
			infoFeatureRegistration = null;
		}
		
		if (itemFeatureRegistration != null)
		{
			itemFeatureRegistration.unregister();
			itemFeatureRegistration = null;
		}
		
	}

}
