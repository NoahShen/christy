package com.google.code.christy.sm.userfavoriteshop;

public interface UserFavoriteShopDbHelper
{
	/**
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public UserFavoriteShopEntity[] getAllFavoriteShop(String username) throws Exception;

	/**
	 * 
	 * @param username
	 * @param shopId
	 * @throws Exception
	 */
	public void addFavoriteShop(String username, long shopId) throws Exception;

	/**
	 * 
	 * @param username
	 * @param shopId
	 * @throws Exception
	 */
	public void removeFavoriteShop(String username, long shopId) throws Exception;
}
