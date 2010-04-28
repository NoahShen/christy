/**
 * 
 */
package com.google.code.christy.sm.userfavoriteshop;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Noah
 * 
 */
public class UserFavoriteShopDbHelperTracker extends ServiceTracker
{

	public UserFavoriteShopDbHelperTracker(BundleContext context)
	{
		super(context, UserFavoriteShopDbHelper.class.getName(), null);
	}

	public UserFavoriteShopDbHelper getUserFavoriteShopDbHelper()
	{
		return (UserFavoriteShopDbHelper) getService();
	}
}
