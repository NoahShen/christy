package com.google.code.christy.sm.userfavoriteshop;

public interface UserFavoriteShopDbHelper
{
	/**
	 * 
	 * @param username
	 * @param startIndex
	 * @param max
	 * @return
	 * @throws Exception
	 */
	public UserFavoriteShopEntity[] getAllFavoriteShop(String username, int startIndex, int max) throws Exception;

	/**
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public int getAllFavoriteShopCount(String username) throws Exception;
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
