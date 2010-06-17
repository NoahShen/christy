package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.code.christy.dbhelper.PubSubSubscription;
import com.google.code.christy.dbhelper.PubSubSubscriptionDbHelper;
import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.log.LoggerServiceTracker;

public class PubSubSubscriptionMysqlDbHelper implements PubSubSubscriptionDbHelper
{
	private ConnectionPool connectionPool;

	private LoggerServiceTracker loggerServiceTracker;
	
	private static final String GETPUBSUBSUBSCRIPTIONS_SQL = "SELECT * FROM pubsubsubscription WHERE subscriber = ? AND nodeId = ?";
	
	private static final String GETALLPUBSUBSUBSCRIPTIONS_SQL = "SELECT * FROM pubsubsubscription WHERE subscriber = ?";
	
	private static final String ADDPUBSUBSUBSCRIPTION_SQL = "INSERT INTO pubsubsubscription (serviceId, nodeId, subId, jid, subscriber, subscription) VALUES (?, ?, ?, ?, ?, ?)";
	
	private static final String REMOVEPUBSUBSUBSCRIPTIONS_SQL = "DELETE FROM pubsubsubscription WHERE subscriber = ? AND nodeId = ? AND subId = ?";
	
	/**
	 * @param connectionPool
	 * @param loggerServiceTracker 
	 */
	public PubSubSubscriptionMysqlDbHelper(ConnectionPool connectionPool, 
					LoggerServiceTracker loggerServiceTracker)
	{
		this.connectionPool = connectionPool;
		this.loggerServiceTracker = loggerServiceTracker;
	}
	
	@Override
	public Collection<PubSubSubscription> getPubSubSubscriptions(String subscriber, String nodeId) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = null;
			if (nodeId == null)
			{
				preStat = connection.prepareStatement(GETALLPUBSUBSUBSCRIPTIONS_SQL);
				preStat.setString(1, subscriber);
			}
			else
			{
				preStat = connection.prepareStatement(GETPUBSUBSUBSCRIPTIONS_SQL);
				preStat.setString(1, subscriber);
				preStat.setString(2, nodeId);
			}
			

			ResultSet subscriptionsResultSet = preStat.executeQuery();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + subscriptionsResultSet.toString());
			}
			
			List<PubSubSubscription> pubSubSubscriptionList = new ArrayList<PubSubSubscription>();
			while (subscriptionsResultSet.next())
			{
				PubSubSubscription sub = new PubSubSubscription();
				sub.setServiceId(subscriptionsResultSet.getString("serviceId"));
				sub.setNodeId(subscriptionsResultSet.getString("nodeId"));
				sub.setJid(subscriptionsResultSet.getString("jid"));
				sub.setSubId(subscriptionsResultSet.getString("subId"));
				String subscription = subscriptionsResultSet.getString("subscription");
				if (subscription != null)
				{
					sub.setSubscription(PubSubSubscription.Subscription.valueOf(subscription));
				}
				
				pubSubSubscriptionList.add(sub);
				
			}
			return pubSubSubscriptionList;
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
	public void addPubSubSubscription(PubSubSubscription subscription) throws SQLException
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			
			PreparedStatement preStat = connection.prepareStatement(ADDPUBSUBSUBSCRIPTION_SQL);
			preStat.setString(1, subscription.getServiceId());
			preStat.setString(2, subscription.getNodeId());
			preStat.setString(3, subscription.getSubId());
			preStat.setString(4, subscription.getJid());
			preStat.setString(5, subscription.getSubscriber());
			preStat.setString(6, subscription.getSubscription().name());
			
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
	public void removePubSubSubscription(String jid, String nodeId, String subId) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(REMOVEPUBSUBSUBSCRIPTIONS_SQL);
			preStat.setString(1, jid);
			preStat.setString(2, nodeId);
			preStat.setString(3, subId);

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
