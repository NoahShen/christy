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
	
	private static final String GETCOUNTOFFAVORITESHOP_SQL = "SELECT count(*) FROM userfavoriteshop WHERE username = ?";
	
	private static final String GETFAVORITESHOP_SQL = "SELECT R.id as id, R.username as username, R.shopId as shopId, title, street, tel" +
					" FROM (SELECT * FROM userfavoriteshop WHERE username = ?) R" +
					" LEFT JOIN shop S ON R.shopId = S.shopId" +
					" LIMIT ?, ?";

	private static final String ADDFAVORITESHOP_SQL = "INSERT INTO userfavoriteshop (username, shopId) VALUES (?, ?)";

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
	public UserFavoriteShopEntity[] getAllFavoriteShop(String username, int startIndex, int max) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETFAVORITESHOP_SQL);
			preStat.setString(1, username);
			preStat.setInt(2, startIndex);
			preStat.setInt(3, max);
			ResultSet resultSet = preStat.executeQuery();
			
			List<UserFavoriteShopEntity> entities = new ArrayList<UserFavoriteShopEntity>();
			while (resultSet.next())
			{
//				String username2 = resultSet.getString("username");
				long id = resultSet.getLong("id");
				long shopId = resultSet.getLong("shopId");
				String name = resultSet.getString("title");
				String street = resultSet.getString("street");
				String tel = resultSet.getString("tel");
				
				UserFavoriteShopEntity entity = new UserFavoriteShopEntity();
				entity.setId(id);
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
	public int getAllFavoriteShopCount(String username) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETCOUNTOFFAVORITESHOP_SQL);
			preStat.setString(1, username);
			ResultSet resultSet = preStat.executeQuery();
			
			if (resultSet.next())
			{
				int count = resultSet.getInt("count(*)");
				return count;
			}
			
			return 0;
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
