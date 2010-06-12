package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.code.christy.dbhelper.PrivateXmlDbHelper;
import com.google.code.christy.dbhelper.PrivateXmlEntity;
import com.google.code.christy.lib.ConnectionPool;

public class PrivateXmlMysqlDbHelper implements PrivateXmlDbHelper
{
	private ConnectionPool connectionPool;
	
	private static final String GETPRIVATEXML_SQL = "SELECT * FROM privatedata WHERE username = ? AND stanzaKey = ?";
	
	private static final String ADDPRIVATEXML_SQL = "INSERT INTO privatedata VALUES (?, ?, ?)";
	
	private static final String REMOVEPRIVATEXML_SQL = "DELETE FROM privatedata WHERE username = ? AND stanzaKey = ?";
	
	private static final String UPDATEPRIVATEXML_SQL = "UPDATE privatedata SET stanzaXml = ? WHERE username = ? AND stanzaKey = ?";
	/**
	 * @param connectionPool
	 */
	public PrivateXmlMysqlDbHelper(ConnectionPool connectionPool)
	{
		this.connectionPool = connectionPool;
	}

	@Override
	public PrivateXmlEntity getPrivateXml(String username, String stanzaKey) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETPRIVATEXML_SQL);
			preStat.setString(1, username);
			preStat.setString(2, stanzaKey);
			ResultSet resultSet = preStat.executeQuery();
			if (resultSet.next())
			{
				String username2 = resultSet.getString("username");
				String stanzaKey2 = resultSet.getString("stanzaKey");
				String stanzaXml = resultSet.getString("stanzaXml");
				
				PrivateXmlEntity entity = new PrivateXmlEntity();
				entity.setUsername(username2);
				entity.setStanzaKey(stanzaKey2);
				entity.setStanzaXml(stanzaXml);
				return entity;
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
	public void removePrivateXml(PrivateXmlEntity entity) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(REMOVEPRIVATEXML_SQL);
			preStat.setString(1, entity.getUsername());
			preStat.setString(2, entity.getStanzaKey());
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
	public void updatePrivateXml(PrivateXmlEntity entity) throws Exception
	{
		PrivateXmlEntity entity2 = getPrivateXml(entity.getUsername(), entity.getStanzaKey());
		
		
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			if (entity2 == null)
			{
				PreparedStatement preStat = connection.prepareStatement(ADDPRIVATEXML_SQL);
				preStat.setString(1, entity.getUsername());
				preStat.setString(2, entity.getStanzaKey());
				preStat.setString(3, entity.getStanzaXml());
				preStat.executeUpdate();
			}
			else
			{
				PreparedStatement preStat = connection.prepareStatement(UPDATEPRIVATEXML_SQL);
				preStat.setString(1, entity.getStanzaXml());
				preStat.setString(2, entity.getUsername());
				preStat.setString(3, entity.getStanzaKey());
				preStat.executeUpdate();
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
