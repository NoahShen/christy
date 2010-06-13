package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.code.christy.dbhelper.PubSubAffiliation;
import com.google.code.christy.dbhelper.PubSubAffiliationDbHelper;
import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.log.LoggerServiceTracker;

public class PubSubAffiliationMysqlDbHelper implements PubSubAffiliationDbHelper
{
	private ConnectionPool connectionPool;

	private LoggerServiceTracker loggerServiceTracker;
	
	private static final String GETPUBSUBAFFILIATION_SQL = "SELECT * FROM pubsubaffiliation WHERE jid = ? AND nodeId = ?";
	
	private static final String GETALLPUBSUBAFFILIATION_SQL = "SELECT * FROM pubsubaffiliation WHERE jid = ?";
	
	/**
	 * @param connectionPool
	 * @param loggerServiceTracker 
	 */
	public PubSubAffiliationMysqlDbHelper(ConnectionPool connectionPool, 
					LoggerServiceTracker loggerServiceTracker)
	{
		this.connectionPool = connectionPool;
		this.loggerServiceTracker = loggerServiceTracker;
	}
	
	@Override
	public Collection<PubSubAffiliation> getPubSubAffiliation(String jid, String nodeId) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = null;
			if (nodeId == null)
			{
				preStat = connection.prepareStatement(GETALLPUBSUBAFFILIATION_SQL);
				preStat.setString(1, jid);
			}
			else
			{
				preStat = connection.prepareStatement(GETPUBSUBAFFILIATION_SQL);
				preStat.setString(1, jid);
				preStat.setString(2, nodeId);
			}
			
			
			
			ResultSet pubSubAffiliationResultSet = preStat.executeQuery();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + pubSubAffiliationResultSet.toString());
			}
			
			List<PubSubAffiliation> pubSubAffiliations = new ArrayList<PubSubAffiliation>();
			while (pubSubAffiliationResultSet.next())
			{
				PubSubAffiliation affi = new PubSubAffiliation();
				affi.setServiceId(pubSubAffiliationResultSet.getString("serviceId"));
				affi.setNodeId(pubSubAffiliationResultSet.getString("nodeId"));
				affi.setJid(pubSubAffiliationResultSet.getString("jid"));
				String affiliationStr = pubSubAffiliationResultSet.getString("affiliation");
				if (affiliationStr != null)
				{
					affi.setAffiliationType(PubSubAffiliation.AffiliationType.valueOf(affiliationStr));
				}
				
				pubSubAffiliations.add(affi);
				
			}
			return pubSubAffiliations;
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
