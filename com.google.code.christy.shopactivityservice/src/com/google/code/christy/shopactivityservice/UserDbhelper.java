package com.google.code.christy.shopactivityservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.log.LoggerServiceTracker;

public class UserDbhelper
{
	private static final String ADDUSER_SQL = "INSERT INTO user (username, password, email, creationDate)" +
						" VALUES (?, ?, ?, NOW())";
	private static final String GETUSERBYNAME_SQL = "SELECT username FROM user WHERE username = ?";
	
	private static final String GETUSERBYEMAIL_SQL = "SELECT username FROM user WHERE email = ?";
	
	private static final String GETEMAILBYNAME_SQL = "SELECT email FROM user WHERE username = ?";
	
	private ConnectionPool connectionPool;
	
	private LoggerServiceTracker loggerServiceTracker;
	
	/**
	 * @param connectionPool
	 */
	public UserDbhelper(LoggerServiceTracker loggerServiceTracke, ConnectionPool connectionPool)
	{
		this.loggerServiceTracker = loggerServiceTracke;
		this.connectionPool = connectionPool;
	}


	public void addUser(String username, String password, String email) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(ADDUSER_SQL);
			preStat.setString(1, username);
			preStat.setString(2, password);
			preStat.setString(3, email);
			preStat.executeUpdate();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
			}
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}
	
	public String getEmailByUsername(String username) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETEMAILBYNAME_SQL);
			preStat.setString(1, username);
			ResultSet emailRs = preStat.executeQuery();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("ResultSet:" + emailRs.toString());
			}
			
			if (emailRs.next())
			{
				return emailRs.getString("email");
			}
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
		return null;
	}


	public int contain(String username, String email) throws SQLException
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETUSERBYNAME_SQL);
			preStat.setString(1, username);
			ResultSet set = preStat.executeQuery();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
			}
			if (set.next()) 
			{
				return 1;
			}
			
			
			
			PreparedStatement preStat2 = connection.prepareStatement(GETUSERBYEMAIL_SQL);
			preStat2.setString(1, email);
			ResultSet set2 = preStat2.executeQuery();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat2.toString());
			}
			if (set2.next()) {
				return 2;
			}
			
			
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
		return 0;
	}
	
	
}
