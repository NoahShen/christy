package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.google.code.christy.dbhelper.User;
import com.google.code.christy.dbhelper.UserDbHelper;
import com.google.code.christy.lib.ConnectionPool;

public class UserMysqlDbHelper implements UserDbHelper
{
	private ConnectionPool connectionPool;
	
	private static final String ADDUSER_SQL = "INSERT INTO user (username, password, creationDate)" +
						" VALUES (?, ?, NOW())";
	
	private static final String GETUSER_SQL = "SELECT * FROM user WHERE username = ?";
	
	private static final String REMOVEUSER_SQL = "DELETE FROM user WHERE username = ?";
	
	private static final String UPDATEUSER_SQL = "UPDATE user SET password = ? WHERE username = ?";
	/**
	 * @param connectionPool
	 */
	public UserMysqlDbHelper(ConnectionPool connectionPool)
	{
		this.connectionPool = connectionPool;
	}

	@Override
	public void addUser(User user) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(ADDUSER_SQL);
			preStat.setString(1, user.getUsername());
			preStat.setString(2, user.getPassword());
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
	public User getUser(String username) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETUSER_SQL);
			preStat.setString(1, username);
			ResultSet users = preStat.executeQuery();
			User user = null;
			if (users.next()) 
			{
				user = new User();
				String password = users.getString(2);
				user.setPassword(password);
				Date createdate = users.getDate(3);
				user.setCreationDate(createdate.getTime());
				Timestamp modifyTimestamp = users.getTimestamp(4);
				user.setModificationDate(modifyTimestamp.getTime());
			}
			return user;
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
	public void removeUser(String username) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(REMOVEUSER_SQL);
			preStat.setString(1, username);
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
	public void updateUserPlainPassword(String username, String newPassword) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(UPDATEUSER_SQL);
			preStat.setString(1, newPassword);
			preStat.setString(2, username);
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
