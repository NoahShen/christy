package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.google.code.christy.dbhelper.LastPublishTime;
import com.google.code.christy.dbhelper.LastPublishTimeDbHelper;
import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.log.LoggerServiceTracker;

public class LastPublishTimeMysqlDbHelper implements LastPublishTimeDbHelper
{
	private ConnectionPool connectionPool;

	private LoggerServiceTracker loggerServiceTracker;
	
	private static final String GETLASTPUBLISHTIME_SQL = "SELECT * FROM lastpublishtime";
	
	private static final String ADDLASTPUBLISHTIME_SQL = "INSERT INTO lastpublishtime VALUES (?)";
	
	private static final String UPDATELASTPUBLISHTIME_SQL = "UPDATE lastpublishtime SET time = ?";
	
	/**
	 * @param connectionPool
	 */
	public LastPublishTimeMysqlDbHelper(ConnectionPool connectionPool,
					LoggerServiceTracker loggerServiceTracker)
	{
		this.connectionPool = connectionPool;
		this.loggerServiceTracker = loggerServiceTracker;
	}
	
	@Override
	public LastPublishTime getLastPublishTime() throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETLASTPUBLISHTIME_SQL);
			ResultSet resultSet = preStat.executeQuery();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + resultSet.toString());
			}
			
			if (resultSet.next())
			{
				Timestamp timestamp = resultSet.getTimestamp("time");
				if (timestamp != null)
				{
					LastPublishTime lastPublishTime = new LastPublishTime(timestamp.getTime());
					return lastPublishTime;
				}
			}
			else
			{
				PreparedStatement preStat2 = connection.prepareStatement(ADDLASTPUBLISHTIME_SQL);
				preStat2.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
				preStat2.executeUpdate();
				
				if (loggerServiceTracker.isDebugEnabled())
				{
					loggerServiceTracker.debug("SQL:" + preStat2.toString());
				}
				return getLastPublishTime();
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

	@Override
	public void updateLastPublishTime(long time) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(UPDATELASTPUBLISHTIME_SQL);
			preStat.setTimestamp(1, new Timestamp(time));
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
