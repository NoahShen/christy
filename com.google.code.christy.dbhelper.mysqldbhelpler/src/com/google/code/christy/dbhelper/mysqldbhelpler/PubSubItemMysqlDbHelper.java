package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
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
				Time creationDate = itemResultSet.getTime("creationDate");
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
				Time creationDate = itemResultSet.getTime("creationDate");
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

}
