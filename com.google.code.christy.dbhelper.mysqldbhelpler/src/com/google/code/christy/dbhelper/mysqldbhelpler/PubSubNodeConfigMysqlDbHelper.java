package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;

import com.google.code.christy.dbhelper.PubSubNodeConfig;
import com.google.code.christy.dbhelper.PubSubNodeConfigDbHelper;
import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.log.LoggerServiceTracker;

public class PubSubNodeConfigMysqlDbHelper implements PubSubNodeConfigDbHelper
{
	private ConnectionPool connectionPool;

	private LoggerServiceTracker loggerServiceTracker;
	
	private static final String GETPUBSUBNODECONFIG_SQL = "SELECT * FROM pubsubnodeconfig WHERE nodeId = ?";
	
	
	/**
	 * @param connectionPool
	 * @param loggerServiceTracker 
	 */
	public PubSubNodeConfigMysqlDbHelper(ConnectionPool connectionPool, 
					LoggerServiceTracker loggerServiceTracker)
	{
		this.connectionPool = connectionPool;
		this.loggerServiceTracker = loggerServiceTracker;
	}
	
	@Override
	public PubSubNodeConfig getPubSubNodeConfig(String nodeId) throws Exception
	{
		Connection connection = null;
		
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETPUBSUBNODECONFIG_SQL);
			preStat.setString(1, nodeId);
			ResultSet configResultSet = preStat.executeQuery();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + configResultSet.toString());
			}
			
			if (configResultSet.next())
			{
				PubSubNodeConfig config = new PubSubNodeConfig();
				config.setServiceId(configResultSet.getString("serviceId"));
				config.setNodeId(configResultSet.getString("nodeId"));
				config.setSubscribeModel(configResultSet.getString("subscribeModel"));
				config.setDeliverPayloads(configResultSet.getBoolean("deliverPayloads"));
				config.setMaxItems(configResultSet.getInt("maxItems"));
				config.setMaxPayloadSize(configResultSet.getInt("maxPayloadSize"));
				config.setNotifyConfigChanges(configResultSet.getBoolean("notifyConfigChanges"));
				config.setNotifyDelete(configResultSet.getBoolean("notifyDelete"));
				config.setNotifyRetract(configResultSet.getBoolean("notifyRetract"));
				config.setPersistItems(configResultSet.getBoolean("persistItems"));
				config.setPublisherModel(configResultSet.getString("publisherModel"));
				config.setSendItemSubscribe(configResultSet.getBoolean("sendItemSubscribe"));
				
				Time modificationDate = configResultSet.getTime("modificationDate");
				if (modificationDate != null)
				{
					config.setModificationDate(modificationDate.getTime());
				}
				
				Time creationDate = configResultSet.getTime("creationDate");
				if (creationDate != null)
				{
					config.setCreationDate(creationDate.getTime());
				}

				return config;
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
