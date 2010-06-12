package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.code.christy.dbhelper.PubSubNode;
import com.google.code.christy.dbhelper.PubSubNodeDbHelper;
import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.log.LoggerServiceTracker;

public class PubSubNodeMysqlDbHelper implements PubSubNodeDbHelper
{
	private ConnectionPool connectionPool;

	private LoggerServiceTracker loggerServiceTracker;
	
	private static final String GETPUBSUBNODE_SQL = "SELECT * FROM pubsubnode WHERE nodeId = ?";
	
	private static final String GETPUBSUBNODES_SQL = "SELECT * FROM pubsubnode WHERE parent = ?";
	
	private static final String GETROOTPUBSUBNODES_SQL = "SELECT * FROM pubsubnode WHERE parent IS NULL";
	/**
	 * @param connectionPool
	 * @param loggerServiceTracker 
	 */
	public PubSubNodeMysqlDbHelper(ConnectionPool connectionPool, 
					LoggerServiceTracker loggerServiceTracker)
	{
		this.connectionPool = connectionPool;
		this.loggerServiceTracker = loggerServiceTracker;
	}
	
	@Override
	public Collection<PubSubNode> getNodes(String parent) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = null;
			if (parent == null)
			{
				preStat = connection.prepareStatement(GETROOTPUBSUBNODES_SQL);
			}
			else
			{
				preStat = connection.prepareStatement(GETPUBSUBNODES_SQL);
				preStat.setString(1, parent);
			}
			
			
			
			ResultSet nodeResultSet = preStat.executeQuery();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + nodeResultSet.toString());
			}
			
			List<PubSubNode> nodeList = new ArrayList<PubSubNode>();
			while (nodeResultSet.next())
			{
				PubSubNode node = new PubSubNode();
				node.setServiceId(nodeResultSet.getString("serviceId"));
				node.setNodeId(nodeResultSet.getString("nodeId"));
				node.setParent(parent);
				node.setName(nodeResultSet.getString("name"));
				node.setLeaf(nodeResultSet.getBoolean("leaf"));
				
				Time modificationDate = nodeResultSet.getTime("modificationDate");
				if (modificationDate != null)
				{
					node.setModificationDate(modificationDate.getTime());
				}
				
				Time creationDate = nodeResultSet.getTime("creationDate");
				if (creationDate != null)
				{
					node.setCreationDate(creationDate.getTime());
				}
				
				node.setCreator(nodeResultSet.getString("creator"));
				node.setDescription(nodeResultSet.getString("description"));
				nodeList.add(node);
				
			}
			return nodeList;
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
	public PubSubNode getNode(String nodeId) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETPUBSUBNODE_SQL);
			preStat.setString(1, nodeId);
			ResultSet nodeResultSet = preStat.executeQuery();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + nodeResultSet.toString());
			}
			
			if (nodeResultSet.next())
			{
				PubSubNode node = new PubSubNode();
				node.setServiceId(nodeResultSet.getString("serviceId"));
				node.setNodeId(nodeResultSet.getString("nodeId"));
				node.setParent(nodeResultSet.getString("parent"));
				node.setName(nodeResultSet.getString("name"));
				node.setLeaf(nodeResultSet.getBoolean("leaf"));
				
				Time modificationDate = nodeResultSet.getTime("modificationDate");
				if (modificationDate != null)
				{
					node.setModificationDate(modificationDate.getTime());
				}
				
				Time creationDate = nodeResultSet.getTime("creationDate");
				if (creationDate != null)
				{
					node.setCreationDate(creationDate.getTime());
				}
				
				node.setCreator(nodeResultSet.getString("creator"));
				node.setDescription(nodeResultSet.getString("description"));
				return node;
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
