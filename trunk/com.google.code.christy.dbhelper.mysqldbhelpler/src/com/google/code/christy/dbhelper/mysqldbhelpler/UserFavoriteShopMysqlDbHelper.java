package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.sm.userfavoriteshop.UserFavoriteShopDbHelper;
import com.google.code.christy.sm.userfavoriteshop.UserFavoriteShopEntity;

public class UserFavoriteShopMysqlDbHelper implements UserFavoriteShopDbHelper
{
	private ConnectionPool connectionPool;

	private static final String GETFAVORITESHOP_SQL = "SELECT R.username as username, R.shopId as shopId, title, street, tel FROM (SELECT * FROM userfavoriteshop WHERE username = ?) R LEFT JOIN shop S ON R.shopId = S.shopId";

	private static final String ADDFAVORITESHOP_SQL = "INSERT INTO userfavoriteshop VALUES (?, ?)";

	private static final String REMOVEFAVORITESHOP_SQL = "DELETE FROM userfavoriteshop WHERE username = ? AND shopId = ?";

	public UserFavoriteShopMysqlDbHelper(ConnectionPool connectionPool)
	{
		super();
		this.connectionPool = connectionPool;
	}

	@Override
	public void addFavoriteShop(String username, long shopId) throws Exception
	{

		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(ADDFAVORITESHOP_SQL);
			preStat.setString(1, username);
			preStat.setLong(2, shopId);
			preStat.executeUpdate();
			
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}

	@Override
	public UserFavoriteShopEntity[] getAllFavoriteShop(String username) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETFAVORITESHOP_SQL);
			preStat.setString(1, username);
			ResultSet resultSet = preStat.executeQuery();
			
			List<UserFavoriteShopEntity> entities = new ArrayList<UserFavoriteShopEntity>();
			while (resultSet.next())
			{
//				String username2 = resultSet.getString("username");
				long shopId = resultSet.getLong("shopId");
				String name = resultSet.getString("title");
				String street = resultSet.getString("street");
				String tel = resultSet.getString("tel");
				
				UserFavoriteShopEntity entity = new UserFavoriteShopEntity();
				entity.setShopId(shopId);
				entity.setUsername(username);
				entity.setShopName(name);
				entity.setStreet(street);
				entity.setTel(tel);
				
				entities.add(entity);
			}
			
			return entities.toArray(new UserFavoriteShopEntity[]{});
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}

	@Override
	public void removeFavoriteShop(String username, long shopId) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(REMOVEFAVORITESHOP_SQL);
			preStat.setString(1, username);
			preStat.setLong(2, shopId);
			preStat.executeUpdate();
			
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
