package com.google.code.christy.dbhelper.mysqldbhelpler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.google.code.christy.dbhelper.RosterItem;
import com.google.code.christy.dbhelper.RosterItemDbHelper;
import com.google.code.christy.dbhelper.RosterItem.Ask;
import com.google.code.christy.dbhelper.RosterItem.Subscription;
import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.xmpp.JID;

public class RosterItemMysqlDbHelper implements RosterItemDbHelper
{
	private ConnectionPool connectionPool;
	
	private static final String ADDROSTERITEM_SQL = "INSERT INTO userroster (username, jid, name, subscription, ask)" +
							" VALUES (?, ?, ?, ?, ?)";
	
	private static final String ADDROSTERITEMGROUP_SQL = "INSERT INTO userrostergroup (rosterId, groupName)" +
							" (SELECT rosterId, ? FROM userroster WHERE username = ? AND jid=?)";
	
	
	private static final String GETROSTERITEM_SQL = "SELECT * FROM (SELECT * FROM userroster WHERE userroster.username = ? ) R" +
							" LEFT JOIN userrostergroup G ON R.rosterId = G.rosterId";
	
	private static final String GETONEROSTERITEM_SQL = "SELECT * FROM (SELECT * FROM userroster WHERE userroster.username = ? AND userroster.jid = ?) R" +
							" LEFT JOIN userrostergroup G ON R.rosterId = G.rosterId";
	
	private static final String REMOVEROSTERITEM_SQL = "DELETE userroster, userrostergroup FROM userroster" +
							" LEFT JOIN userrostergroup ON userroster.rosterID = userrostergroup.rosterID" +
							" WHERE userroster.username=? AND userroster.jid=?";
	
	private static final String UPDATEROSTERITEM_SQL = "UPDATE userroster SET subscription = ?, name = ?, ask = ? WHERE username = ? AND jid = ?";
	
	private static final String DELETEGROUP_SQL = "DELETE FROM userrostergroup WHERE rosterId IN (SELECT rosterId FROM userroster WHERE username = ? AND jid=?)";
	
	
	private static final String UPDATEASK_SQL = "UPDATE userroster SET ask = ? WHERE username = ? AND jid = ?";

	private static final String UPDATENAME_SQL = "UPDATE userroster SET name = ? WHERE username = ? AND jid = ?";
	
	private static final String UPDATESUBSCRIPTION_SQL = "UPDATE userroster SET subscription = ? WHERE username = ? AND jid = ?";
	
	/**
	 * @param connectionPool
	 */
	public RosterItemMysqlDbHelper(ConnectionPool connectionPool)
	{
		this.connectionPool = connectionPool;
	}

	@Override
	public void addRosterItem(RosterItem rosterItem) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			connection.setAutoCommit(false);
			
			PreparedStatement preStat = connection.prepareStatement(ADDROSTERITEM_SQL);
			preStat.setString(1, rosterItem.getUsername());
			preStat.setString(2, rosterItem.getRosterJID().toBareJID());
			preStat.setString(3, rosterItem.getNickname());
			preStat.setString(4, rosterItem.getSubscription().name());
			RosterItem.Ask ask = rosterItem.getAsk();
			if (ask == null)
			{
				preStat.setNull(5, Types.NULL);
			}
			else 
			{
				preStat.setString(5, ask.name());
			}

			preStat.executeUpdate();
			
			String[] groups = rosterItem.getGroups();
			for (int i = 0; i < groups.length; ++i)
			{
				String group = groups[i];
				PreparedStatement preStatGroup = connection.prepareStatement(ADDROSTERITEMGROUP_SQL);
				preStatGroup.setString(1, group);
				preStatGroup.setString(2, rosterItem.getUsername());
				preStatGroup.setString(3, rosterItem.getRosterJID().toBareJID());
				preStatGroup.executeUpdate();
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
	public RosterItem[] getRosterItems(String username) throws Exception
	{
		Connection connection = null;
		try
		{

			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETROSTERITEM_SQL);
			preStat.setString(1, username);
			
			Map<Integer, RosterItem> items  = new HashMap<Integer, RosterItem>();
			ResultSet rosterItems = preStat.executeQuery();
			while (rosterItems.next())
			{
				int rosterId = rosterItems.getInt(1);
				RosterItem item = items.get(rosterId);
				if (item == null)
				{
					item = new RosterItem();
					item.setUsername(username);
					item.setRosterJID(new JID(rosterItems.getString(3)));
					item.setNickname(rosterItems.getString(4));
					String ask = rosterItems.getString(5);
					if (ask != null)
					{
						item.setAsk(RosterItem.Ask.valueOf(ask));
					}
					item.setSubscription(RosterItem.Subscription.valueOf(rosterItems.getString(6)));
					items.put(rosterId, item);
				}
				String group = rosterItems.getString(8);
				item.addGroup(group);
			}
			
			return items.values().toArray(new RosterItem[]{});
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
	public void removeRosterItem(RosterItem rosterItem) throws Exception
	{
		removeRosterItem(rosterItem.getUsername(), rosterItem.getRosterJID());
	}

	@Override
	public void updateRosterItem(RosterItem rosterItem) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			connection.setAutoCommit(false);
			PreparedStatement preStat = connection.prepareStatement(UPDATEROSTERITEM_SQL);
			preStat.setString(1, rosterItem.getSubscription().name());
			preStat.setString(2, rosterItem.getNickname());
			RosterItem.Ask ask = rosterItem.getAsk();
			if (ask == null)
			{
				preStat.setNull(3, Types.NULL);
			}
			else 
			{
				preStat.setString(3, ask.name());
			}
			preStat.setString(4, rosterItem.getUsername());
			preStat.setString(5, rosterItem.getRosterJID().toBareJID());
			preStat.executeUpdate();
			
			PreparedStatement preStatDeleteGroup = connection.prepareStatement(DELETEGROUP_SQL);
			preStatDeleteGroup.setString(1, rosterItem.getUsername());
			preStatDeleteGroup.setString(2, rosterItem.getRosterJID().toBareJID());
			preStatDeleteGroup.executeUpdate();
			
			String[] groups = rosterItem.getGroups();
			for (int i = 0; i < groups.length; ++i)
			{
				String group = groups[i];
				PreparedStatement preStatGroup = connection.prepareStatement(ADDROSTERITEMGROUP_SQL);
				preStatGroup.setString(1, group);
				preStatGroup.setString(2, rosterItem.getUsername());
				preStatGroup.setString(3, rosterItem.getRosterJID().toBareJID());
				preStatGroup.executeUpdate();
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
	public RosterItem getRosterItem(String username, JID rosterJID) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETONEROSTERITEM_SQL);
			preStat.setString(1, username);
			preStat.setString(2, rosterJID.toBareJID());

			ResultSet rosterItems = preStat.executeQuery();
			RosterItem item = null;
			while (rosterItems.next())
			{
				if (item == null)
				{
					item = new RosterItem();
					item.setUsername(username);
					item.setRosterJID(new JID(rosterItems.getString(3)));
					item.setNickname(rosterItems.getString(4));
					String ask = rosterItems.getString(5);
					if (ask != null)
					{
						item.setAsk(RosterItem.Ask.valueOf(ask));
					}
					item.setSubscription(RosterItem.Subscription.valueOf(rosterItems.getString(6)));
				}
				String group = rosterItems.getString(8);
				item.addGroup(group);
				
			}
			return item;
			
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
	public void removeRosterItem(String username, JID rosterJID) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(REMOVEROSTERITEM_SQL);
			preStat.setString(1, username);
			preStat.setString(2, rosterJID.toBareJID());
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
	public void updateRosterItemAsk(String username, JID rosterJID, Ask ask) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(UPDATEASK_SQL);
			preStat.setString(1, ask.name());
			preStat.setString(2, username);
			preStat.setString(3, rosterJID.toBareJID());
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
	public void updateRosterItemGroups(String username, JID rosterJID, String[] groups) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			connection.setAutoCommit(false);
			PreparedStatement preStatDeleteGroup = connection.prepareStatement(DELETEGROUP_SQL);
			preStatDeleteGroup.setString(1, username);
			preStatDeleteGroup.setString(2, rosterJID.toBareJID());
			preStatDeleteGroup.executeUpdate();
			
			for (int i = 0; i < groups.length; ++i)
			{
				String group = groups[i];
				PreparedStatement preStatGroup = connection.prepareStatement(ADDROSTERITEMGROUP_SQL);
				preStatGroup.setString(1, group);
				preStatGroup.setString(2, username);
				preStatGroup.setString(3, rosterJID.toBareJID());
				preStatGroup.executeUpdate();
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
	public void updateRosterItemNickname(String username, JID rosterJID, String nickname) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(UPDATENAME_SQL);
			preStat.setString(1, nickname);
			preStat.setString(2, username);
			preStat.setString(3, rosterJID.toBareJID());
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
	public void updateRosterItemSubscription(String username, JID rosterJID, Subscription subscription) throws Exception
	{
		
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(UPDATESUBSCRIPTION_SQL);
			preStat.setString(1, subscription.name());
			preStat.setString(2, username);
			preStat.setString(3, rosterJID.toBareJID());
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

}
