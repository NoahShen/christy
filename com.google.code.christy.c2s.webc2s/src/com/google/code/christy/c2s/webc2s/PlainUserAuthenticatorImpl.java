/**
 * 
 */
package com.google.code.christy.c2s.webc2s;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.code.christy.c2s.ClientSession;
import com.google.code.christy.c2s.UnauthorizedException;
import com.google.code.christy.c2s.UserAuthenticator;
import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.util.StringUtils;



/**
 * @author noah
 *
 */
public class PlainUserAuthenticatorImpl implements UserAuthenticator
{

	private static final String GETUSER_SQL = "SELECT COUNT(*) FROM user WHERE username = ? AND password = ?";
	
	private ConnectionPool connectionPool;
	
	/**
	 * @param connectionPool
	 */
	public PlainUserAuthenticatorImpl(ConnectionPool connectionPool)
	{
		this.connectionPool = connectionPool;
	}

	@Override
	public String getMechanismName()
	{
		return "PLAIN";
	}
	
	@Override
	public void authenticate(ClientSession clientSession, String content) throws UnauthorizedException
	{
		Connection connection = null;
		try
		{
			String decodedContent = new String(StringUtils.decodeBase64(content), "UTF-8");
			String[] splits = decodedContent.split("\0");
			if (splits.length != 3)
			{
				throw new UnauthorizedException();
			}
			
			String username = splits[1];
			String password = splits[2];
			
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETUSER_SQL);
			preStat.setString(1, username);
			preStat.setString(2, password);
			ResultSet resultSet = preStat.executeQuery();
			if (resultSet.next())
			{
				int count = resultSet.getInt("COUNT(*)");
				if (count == 0)
				{
					throw new UnauthorizedException();
				}
			}
			else
			{
				throw new UnauthorizedException();
			}
			
			clientSession.setUsername(username);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public void response(ClientSession clientSession, String content) throws UnauthorizedException
	{
		// TODO Auto-generated method stub
		
	}


}
