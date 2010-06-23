package com.google.code.christy.shopactivityservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.log.LoggerServiceTracker;

public class ActivityDbHelper
{
	private static final String GETACTIVITYBYLOC_SQL = "SELECT *, SQRT(POW(? - easting, 2) + POW(? - northing, 2)) AS distance" +
								" FROM activity HAVING distance <= (? * 1000) ORDER BY distance LIMIT ?, ?";
	
	private static final String GETACTIVITYBYLOCCOUNT_SQL = "SELECT COUNT(*) FROM (SELECT *, SQRT(POW(? - easting, 2) + POW(? - northing, 2)) AS distance FROM activity HAVING distance <= (? * 1000)) R";

	private static final String GETACTIVITYETAIL_SQL = "SELECT * FROM activity WHERE activityId = ?";
	
	private ConnectionPool connectionPool;
	
	private LoggerServiceTracker loggerServiceTracker;
	
	public ActivityDbHelper(LoggerServiceTracker loggerServiceTracker, ConnectionPool connectionPool)
	{
		super();
		this.loggerServiceTracker = loggerServiceTracker;
		this.connectionPool = connectionPool;
	}
	

	public Object[] getActivityByLoc(String type, int easting, int northing, int distance, int page, int count, boolean getTotal) throws Exception
	{
		Connection connection = null;
		Object[] returnValue = new Object[2];
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETACTIVITYBYLOC_SQL);
			preStat.setInt(1, easting);
			preStat.setInt(2, northing);
			preStat.setInt(3, distance);
			preStat.setInt(4, (page - 1) * count);
			preStat.setInt(5, count);
			
			
			ResultSet activityResSet = preStat.executeQuery();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + activityResSet.toString());
			}
			
			List<Activity> activities = new ArrayList<Activity>();
			while (activityResSet.next()) 
			{
				long activityId = activityResSet.getLong("activityId");
				long shopId = activityResSet.getLong("shopId");
				String title = activityResSet.getString("title");
				String intro = activityResSet.getString("intro");
				String content = activityResSet.getString("content");
				String activityImg = activityResSet.getString("activityImg");
				double longitude = activityResSet.getDouble("longitude");
				double latitude = activityResSet.getDouble("latitude");
				double distanceFromUser = activityResSet.getDouble("distance");
				
				Timestamp startDate = activityResSet.getTimestamp("startDate");
				Timestamp endDate = activityResSet.getTimestamp("endDate");
				
				Activity activity = new Activity();
				activity.setActivityId(activityId);
				activity.setShopId(shopId);
				activity.setTitle(title);
				activity.setIntro(intro);
				activity.setContent(content);
				activity.setActivityImg(activityImg);
				
				if (startDate != null)
				{
					activity.setStartDate(startDate.getTime());
				}
				
				if (endDate != null)
				{
					activity.setEndDate(endDate.getTime());
				}
				
				activity.setDistanceFromUser(distanceFromUser);
				
				activity.setLatitude(latitude);
				activity.setLongitude(longitude);

				activities.add(activity);
			}
			returnValue[1] = activities.toArray(new Activity[]{});
			
			if (getTotal)
			{
				connection = connectionPool.getConnection();
				PreparedStatement preStat2 = connection.prepareStatement(GETACTIVITYBYLOCCOUNT_SQL);
				preStat2.setInt(1, easting);
				preStat2.setInt(2, northing);
				preStat2.setInt(3, distance);
				
				ResultSet activityResSet2 = preStat2.executeQuery();
				if (loggerServiceTracker.isDebugEnabled())
				{
					loggerServiceTracker.debug("SQL:" + preStat2.toString());
					loggerServiceTracker.debug("Result:" + activityResSet2.toString());
				}
				if (activityResSet2.next()) 
				{
					int countResult = activityResSet2.getInt("COUNT(*)");
					returnValue[0] = countResult;
				}
				
				
			}
			
			return returnValue;
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}


	public Activity getActivityByLoc(long activityId) throws SQLException
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETACTIVITYETAIL_SQL);
			preStat.setLong(1, activityId);
			ResultSet activityResSet = preStat.executeQuery();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + activityResSet.toString());
			}

			if (activityResSet.next()) 
			{
				long shopId = activityResSet.getLong("shopId");
				String title = activityResSet.getString("title");
				String intro = activityResSet.getString("intro");
				String content = activityResSet.getString("content");
				String activityImg = activityResSet.getString("activityImg");
				double longitude = activityResSet.getDouble("longitude");
				double latitude = activityResSet.getDouble("latitude");				
				Timestamp startDate = activityResSet.getTimestamp("startDate");
				Timestamp endDate = activityResSet.getTimestamp("endDate");
				
				Activity activity = new Activity();
				activity.setActivityId(activityId);
				activity.setShopId(shopId);
				activity.setTitle(title);
				activity.setIntro(intro);
				activity.setContent(content);
				activity.setActivityImg(activityImg);
				
				if (startDate != null)
				{
					activity.setStartDate(startDate.getTime());
				}
				
				if (endDate != null)
				{
					activity.setEndDate(endDate.getTime());
				}
								
				activity.setLatitude(latitude);
				activity.setLongitude(longitude);
				
				return activity;
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
}
