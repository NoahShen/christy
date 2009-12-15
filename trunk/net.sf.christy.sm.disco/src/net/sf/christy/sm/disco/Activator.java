package net.sf.christy.sm.disco;

import net.sf.christy.sm.disco.impl.DiscoInfoFeatureServiceTracker;
import net.sf.christy.sm.disco.impl.DiscoInfoIdentityServiceTracker;
import net.sf.christy.sm.disco.impl.DiscoItemServiceTracker;
import net.sf.christy.sm.disco.impl.DiscoManagerImpl;
import net.sf.christy.sm.disco.parser.DiscoInfoExtensionParser;
import net.sf.christy.sm.disco.parser.DiscoItemsExtensionParser;
import net.sf.christy.sm.SmHandler;
import net.sf.christy.xmppparser.ExtensionParser;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

	private DiscoInfoFeatureServiceTracker discoInfoFeatureServiceTracker;

	private DiscoInfoIdentityServiceTracker discoInfoIdentityServiceTracker;

	private DiscoItemServiceTracker discoItemServiceTracker;

	private ServiceRegistration discoManagerRegistration;

	private ServiceRegistration infoFeatureRegistration;

	private ServiceRegistration itemFeatureRegistration;

	private ServiceRegistration discoInfoExtensionParserRegistration;

	private ServiceRegistration discoItemsExtensionParserRegistration;

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
		
		
		DiscoInfoExtensionParser discoInfoExtensionParser = new DiscoInfoExtensionParser();
		discoInfoExtensionParserRegistration = 
			context.registerService(ExtensionParser.class.getName(), discoInfoExtensionParser, null);
		
		DiscoItemsExtensionParser discoItemsExtensionParser = new DiscoItemsExtensionParser();
		discoItemsExtensionParserRegistration = 
			context.registerService(ExtensionParser.class.getName(), discoItemsExtensionParser, null);
		
		
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
		
		if (discoInfoExtensionParserRegistration != null)
		{
			discoInfoExtensionParserRegistration.unregister();
			discoInfoExtensionParserRegistration = null;
		}
		
		if (discoItemsExtensionParserRegistration != null)
		{
			discoItemsExtensionParserRegistration.unregister();
			discoItemsExtensionParserRegistration = null;
		}
	}

}
