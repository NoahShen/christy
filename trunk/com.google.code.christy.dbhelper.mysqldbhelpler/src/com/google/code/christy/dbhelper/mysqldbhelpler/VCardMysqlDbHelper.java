package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.sm.vcard.VCardDbHelper;
import com.google.code.christy.sm.vcard.VCardEntity;

public class VCardMysqlDbHelper implements VCardDbHelper
{
	private ConnectionPool connectionPool;
	
	private static final String GETPVCARD_SQL = "SELECT * FROM uservcard WHERE username = ?";
	
	private static final String ADDVCARD_SQL = "INSERT INTO uservcard VALUES (?, ?)";
	
	private static final String REMOVEVCARD_SQL = "DELETE FROM uservcard WHERE username = ?";
	
	private static final String UPDATEVCARD_SQL = "UPDATE uservcard SET vCard = ? WHERE username = ?";
	
	
	
	public VCardMysqlDbHelper(ConnectionPool connectionPool)
	{
		super();
		this.connectionPool = connectionPool;
	}

	@Override
	public VCardEntity getVCardEntity(String username) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETPVCARD_SQL);
			preStat.setString(1, username);
			ResultSet resultSet = preStat.executeQuery();
			if (resultSet.next())
			{
//				String username2 = resultSet.getString("username");
				String vcard = resultSet.getString("vCard");
				VCardEntity entity = new VCardEntity();
				entity.setUsername(username);
				entity.setVCardContent(vcard);
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
	public void removeVCardEntity(String username) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(REMOVEVCARD_SQL);
			preStat.setString(1, username);
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
	public void updateVCardEntity(VCardEntity entity) throws Exception
	{
		VCardEntity entity2 = getVCardEntity(entity.getUsername());
		
		
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			if (entity2 == null)
			{
				PreparedStatement preStat = connection.prepareStatement(ADDVCARD_SQL);
				preStat.setString(1, entity.getUsername());
				preStat.setString(2, entity.getVCardContent());
				preStat.executeUpdate();
			}
			else
			{
				PreparedStatement preStat = connection.prepareStatement(UPDATEVCARD_SQL);
				preStat.setString(1, entity.getVCardContent());
				preStat.setString(2, entity.getUsername());
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
