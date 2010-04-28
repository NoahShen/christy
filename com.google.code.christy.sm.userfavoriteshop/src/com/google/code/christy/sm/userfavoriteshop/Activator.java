package com.google.code.christy.sm.userfavoriteshop;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.sm.SmHandler;
import com.google.code.christy.sm.userfavoriteshop.parser.UserFavoriteShopExtensionParser;
import com.google.code.christy.xmppparser.ExtensionParser;

public class Activator implements BundleActivator
{

	private ServiceRegistration userFavoriteShopExtensionParserRegistration;
	private UserFavoriteShopDbHelperTracker userFavoriteShopDbHelperTracker;
	private ServiceRegistration userFavoriteShopHandlerRegistration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{

		UserFavoriteShopExtensionParser userFavoriteShopExtensionParser = new UserFavoriteShopExtensionParser();
		userFavoriteShopExtensionParserRegistration = context.registerService(ExtensionParser.class.getName(), userFavoriteShopExtensionParser, null);
		
		userFavoriteShopDbHelperTracker = new UserFavoriteShopDbHelperTracker(context);
		userFavoriteShopDbHelperTracker.open();
		
		UserFavoriteShopHandler userFavoriteShopHandler = new UserFavoriteShopHandler(userFavoriteShopDbHelperTracker);
		userFavoriteShopHandlerRegistration = context.registerService(SmHandler.class.getName(), userFavoriteShopHandler, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (userFavoriteShopExtensionParserRegistration != null)
		{
			userFavoriteShopExtensionParserRegistration.unregister();
			userFavoriteShopExtensionParserRegistration = null;
		}
		
		if (userFavoriteShopDbHelperTracker != null)
		{
			userFavoriteShopDbHelperTracker.close();
			userFavoriteShopDbHelperTracker = null;
		}
		
		if (userFavoriteShopHandlerRegistration != null)
		{
			userFavoriteShopHandlerRegistration.unregister();
			userFavoriteShopHandlerRegistration = null;
		}
	}

}
