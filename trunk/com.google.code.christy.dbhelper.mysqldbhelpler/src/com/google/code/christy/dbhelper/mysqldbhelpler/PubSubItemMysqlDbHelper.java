package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.code.christy.dbhelper.PubSubItem;
import com.google.code.christy.dbhelper.PubSubItemDbHelper;
import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.log.LoggerServiceTracker;

public class PubSubItemMysqlDbHelper implements PubSubItemDbHelper
{
	private ConnectionPool connectionPool;

	private LoggerServiceTracker loggerServiceTracker;
	
	private static final String GETPUBSUBITEM_SQL = "SELECT * FROM pubsubitem WHERE nodeId = ? ORDER BY creationDate DESC LIMIT ?";
	
	private static final String GETONEPUBSUBITEM_SQL = "SELECT * FROM pubsubitem WHERE nodeId = ? AND itemId = ?";
	
	private static final String ADDPUBSUBITEM_SQL = "INSERT INTO pubsubitem VALUES (?, ?, ?, ?, ?, NOW())";
	
	private static final String GETONEPUBSUBITEMBYTIME_SQL = "SELECT * FROM pubsubitem WHERE creationDate > ? ORDER BY creationDate DESC";

	
	/**
	 * @param connectionPool
	 * @param loggerServiceTracker 
	 */
	public PubSubItemMysqlDbHelper(ConnectionPool connectionPool, 
					LoggerServiceTracker loggerServiceTracker)
	{
		this.connectionPool = connectionPool;
		this.loggerServiceTracker = loggerServiceTracker;
	}
	
	@Override
	public Collection<PubSubItem> getPubSubItems(String nodeId, int max) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETPUBSUBITEM_SQL);			
			preStat.setString(1, nodeId);
			preStat.setInt(2, max);
			
			ResultSet itemResultSet = preStat.executeQuery();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + itemResultSet.toString());
			}
			
			List<PubSubItem> itemList = new ArrayList<PubSubItem>();
			while (itemResultSet.next())
			{
				PubSubItem item = new PubSubItem();
				item.setServiceId(itemResultSet.getString("serviceId"));
				item.setNodeId(itemResultSet.getString("nodeId"));
				item.setItemId(itemResultSet.getString("itemId"));
				item.setJid(itemResultSet.getString("jid"));
				Timestamp creationDate = itemResultSet.getTimestamp("creationDate");
				if (creationDate != null)
				{
					item.setCreationDate(creationDate.getTime());
				}
				item.setPayload(itemResultSet.getString("payload"));
				
				itemList.add(item);
				
			}
			return itemList;
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
	public PubSubItem getPubSubItem(String nodeId, String itemId) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETONEPUBSUBITEM_SQL);			
			preStat.setString(1, nodeId);
			preStat.setString(2, itemId);
			
			ResultSet itemResultSet = preStat.executeQuery();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + itemResultSet.toString());
			}
			
			if (itemResultSet.next())
			{
				PubSubItem item = new PubSubItem();
				item.setServiceId(itemResultSet.getString("serviceId"));
				item.setNodeId(itemResultSet.getString("nodeId"));
				item.setItemId(itemResultSet.getString("itemId"));
				item.setJid(itemResultSet.getString("jid"));
				Timestamp creationDate = itemResultSet.getTimestamp("creationDate");
				if (creationDate != null)
				{
					item.setCreationDate(creationDate.getTime());
				}
				item.setPayload(itemResultSet.getString("payload"));
				
				return item;
				
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
	public void addPubSubItem(PubSubItem... pubSubItems) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			connection.setAutoCommit(false);
			
			for (PubSubItem pubSubItem : pubSubItems)
			{
				PreparedStatement preStat = connection.prepareStatement(ADDPUBSUBITEM_SQL);
				preStat.setString(1, pubSubItem.getServiceId());
				preStat.setString(2, pubSubItem.getNodeId());
				preStat.setString(3, pubSubItem.getItemId());
				preStat.setString(4, pubSubItem.getJid());
				preStat.setString(5, pubSubItem.getPayload());

				preStat.executeUpdate();
				
				if (loggerServiceTracker.isDebugEnabled())
				{
					loggerServiceTracker.debug("SQL:" + preStat.toString());
				}
			}
						
			connection.commit();
		}
		finally
		{
			if (connection != null)
			{
				connection.setAutoCommit(true);
				connectionPool.returnConnection(connection);
			}
			
		}
	}

	@Override
	public List<PubSubItem> getPubSubItemByTime(long time) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETONEPUBSUBITEMBYTIME_SQL);			
			preStat.setTimestamp(1, new Timestamp(time));
			
			ResultSet itemResultSet = preStat.executeQuery();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + itemResultSet.toString());
			}
			
			List<PubSubItem> itemList = new ArrayList<PubSubItem>();
			while (itemResultSet.next())
			{
				PubSubItem item = new PubSubItem();
				item.setServiceId(itemResultSet.getString("serviceId"));
				item.setNodeId(itemResultSet.getString("nodeId"));
				item.setItemId(itemResultSet.getString("itemId"));
				item.setJid(itemResultSet.getString("jid"));
				Timestamp creationDate = itemResultSet.getTimestamp("creationDate");
				if (creationDate != null)
				{
					item.setCreationDate(creationDate.getTime());
				}
				item.setPayload(itemResultSet.getString("payload"));
				
				itemList.add(item);
				
			}
			return itemList;
			
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
