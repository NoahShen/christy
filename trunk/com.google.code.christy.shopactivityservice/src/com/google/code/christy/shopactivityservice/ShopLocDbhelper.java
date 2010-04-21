/**
 * 
 */
package com.google.code.christy.shopactivityservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.google.code.christy.lib.ConnectionPool;

/**
 * @author Noah
 *
 */
public class ShopLocDbhelper
{
	private ConnectionPool connectionPool;
	
	private static final String GETALLSHOPLOC_SQL = "SELECT * FROM shoploc";
	
	public ShopLocDbhelper(ConnectionPool connectionPool)
	{
		super();
		this.connectionPool = connectionPool;
	}

	public ShopLoc[] getAllShopLoc() throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETALLSHOPLOC_SQL);
			ResultSet shopLocResSet = preStat.executeQuery();
			List<ShopLoc> shoplocs = new ArrayList<ShopLoc>();
			while (shopLocResSet.next()) 
			{
				long id = shopLocResSet.getLong(1);
				double longitude = shopLocResSet.getDouble(2);
				double latitude = shopLocResSet.getDouble(3);
				
				ShopLoc loc = new ShopLoc(id, latitude, longitude);
				shoplocs.add(loc);
			}
			return shoplocs.toArray(new ShopLoc[]{});
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}
}
