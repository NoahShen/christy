package com.google.code.christy.shopactivityservice.subscription;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.log.LoggerServiceTracker;

public class EmailSubscriptionDbHelper
{
	private static final String GETEMAILSUBSCRIPTION_SQL = "SELECT * FROM emailsubscription WHERE username = ?";
	
	private static final String ADDEMAILSUBSCRIPTION_SQL = "INSERT INTO emailsubscription VALUES (?, 1)";

	private static final String REMOVEEMAILSUBSCRIPTION_SQL = "DELETE FROM emailsubscription WHERE username = ?";

	private ConnectionPool connectionPool;
	
	private LoggerServiceTracker loggerServiceTracker;
	
	public EmailSubscriptionDbHelper(LoggerServiceTracker loggerServiceTracker, ConnectionPool connectionPool)
	{
		super();
		this.loggerServiceTracker = loggerServiceTracker;
		this.connectionPool = connectionPool;
	}
	
	public EmailSubscription getEmailSubscription(String username) throws SQLException
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETEMAILSUBSCRIPTION_SQL);
			preStat.setString(1, username);
			ResultSet emailSubscriptionResSet = preStat.executeQuery();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + emailSubscriptionResSet.toString());
			}

			if (emailSubscriptionResSet.next()) 
			{
				boolean receiveNewActivity = emailSubscriptionResSet.getBoolean("receiveNewActivity");
				
				EmailSubscription emailSubscription = new EmailSubscription();
				emailSubscription.setUsername(username);
				emailSubscription.setReceiveNewActivity(receiveNewActivity);
				
				return emailSubscription;
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
	
	public void subscribeEmail(String username) throws SQLException
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(ADDEMAILSUBSCRIPTION_SQL);
			preStat.setString(1, username);
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
	
	
	public void unsubscribeEmail(String username) throws SQLException
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(REMOVEEMAILSUBSCRIPTION_SQL);
			preStat.setString(1, username);
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
}
