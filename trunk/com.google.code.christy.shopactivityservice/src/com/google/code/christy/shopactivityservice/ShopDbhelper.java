/**
 * 
 */
package com.google.code.christy.shopactivityservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.log.LoggerServiceTracker;

/**
 * @author Noah
 *
 */
public class ShopDbhelper
{
	private ConnectionPool connectionPool;
	
	private static final String GETALLSHOP_SQL = "SELECT * FROM shop";
	
	private static final String GETALLSHOPWITHOVERALL_SQL = "SELECT * FROM shop LEFT JOIN shopoverall O ON O.shopId = shop.shopId";
	
	private static final String GETSHOPDETAIL_SQL = "SELECT R.shopId, R.enterpriseUser, R.type, R.title, R.content, R.shopImg, R.district, R.street, R.tel, R.longitude, R.latitude," +
							" O.itemName, O.itemValue" +
							" FROM" +
							" (SELECT * FROM shop WHERE shopId = ?) R" +
							" LEFT JOIN shopoverall O ON O.shopId = R.shopId";
	
	private static final String ADDSHOPCOMMENT_SQL = "INSERT INTO shopcomment (shopId, username, score, content, creationDate) VALUES (?, ?, ?, ?, NOW())";
	
	private static final String ADDSHOPVOTER_SQL = "INSERT INTO shopvoter (username, shopId, itemName, value) VALUES (?, ?, ?, ?)";

	private static final String GETSHOPBYLOC_SQL = "SELECT *, SQRT(POW(? - easting, 2) + POW(? - northing, 2)) AS distance" +
						" FROM shop WHERE type = ? HAVING distance<=(? * 1000) ORDER BY distance LIMIT ?, ?";
	
	private static final String GETSHOPBYLOCCOUNT_SQL = "SELECT COUNT(*) FROM (SELECT *, SQRT(POW(? - easting, 2) + POW(? - northing, 2)) AS distance FROM shop WHERE type = ? HAVING distance<=(? * 1000)) R";
	
	private static final String GETSHOPBYLOCWITHOUTTYPE_SQL = "SELECT *, SQRT(POW(? - easting, 2) + POW(? - northing, 2)) AS distance" +
						" FROM shop HAVING distance<=(? * 1000) ORDER BY distance LIMIT ?, ?";

	private static final String GETSHOPBYLOCCOUNTWITHOUTTYPE_SQL = "SELECT COUNT(*) FROM (SELECT *, SQRT(POW(? - easting, 2) + POW(? - northing, 2)) AS distance FROM shop HAVING distance<=(? * 1000)) R";
	
	private static final String GETSHOPBYKEY_SQL = "SELECT * FROM shop WHERE title LIKE ? OR  content LIKE ? OR district LIKE ? OR street LIKE ? LIMIT ?, ?";

	private static final String GETSHOPBYKEYCOUNT_SQL = "SELECT COUNT(*) FROM (SELECT * FROM shop WHERE title LIKE ? OR  content LIKE ? OR district LIKE ? OR street LIKE ?) R";


	private static final String GETSHOPCOOMENTS_SQL = "SELECT * FROM shopcomment WHERE shopId = ? ORDER BY modificationDate DESC LIMIT ?, ?";
	
	private static final String GETUSERSHOPCOOMENTS_SQL = "SELECT R.shopId, R.score, R.content, R.modificationDate, S.title FROM (SELECT * FROM shopcomment WHERE username = ?) R LEFT JOIN shop S ON S.shopId = R.shopId ORDER BY R.modificationDate DESC LIMIT ?, ?";
	
	private static final String GETUSERSHOPCOOMENTSCOUNT_SQL = "SELECT COUNT(*) FROM shopcomment WHERE username = ?";
	
	private static final String GETCOUNTOFFAVORITESHOP_SQL = "SELECT COUNT(*) FROM userfavoriteshop WHERE username = ?";
	
	private static final String GETFAVORITESHOP_SQL = "SELECT R.id as id, R.username as username, R.shopId as shopId, title, street, tel" +
							" FROM (SELECT * FROM userfavoriteshop WHERE username = ?) R" +
							" LEFT JOIN shop S ON R.shopId = S.shopId" +
							" LIMIT ?, ?";
	
	private static final String REMOVEFAVORITESHOP_SQL = "DELETE FROM userfavoriteshop WHERE username = ? AND id = ?";

	private static final String ADDFAVORITESHOP_SQL = "INSERT INTO userfavoriteshop (username, shopId) VALUES (?, ?)";

	private static final String CONTAINFAVORITESHOP_SQL = "SELECT * FROM userfavoriteshop WHERE username = ? AND shopId = ?";
	
	private LoggerServiceTracker loggerServiceTracker;
	
	public ShopDbhelper(LoggerServiceTracker loggerServiceTracker, ConnectionPool connectionPool)
	{
		super();
		this.loggerServiceTracker = loggerServiceTracker;
		this.connectionPool = connectionPool;
	}

	public Shop[] getAllShop() throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETALLSHOP_SQL);
			ResultSet shopResSet = preStat.executeQuery();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + shopResSet.toString());
			}
			List<Shop> shops = new ArrayList<Shop>();
			while (shopResSet.next()) 
			{
				long id = shopResSet.getLong("shopId");
				String eusername = shopResSet.getString("enterpriseUser");
				String type = shopResSet.getString("type");
				String title = shopResSet.getString("title");
				String content = shopResSet.getString("content");
				String shopImg = shopResSet.getString("shopImg");
				String district = shopResSet.getString("district");
				String street = shopResSet.getString("street");
				String tel = shopResSet.getString("tel");
				double longitude = shopResSet.getDouble("longitude");
				double latitude = shopResSet.getDouble("latitude");
				
				Shop shop = new Shop();
				shop.setShopId(id);
				shop.setEusername(eusername);
				shop.setType(type);
				shop.setTitle(title);
				shop.setContent(content);
				shop.setShopImg(shopImg);
				shop.setDistrict(district);
				shop.setStreet(street);
				shop.setTel(tel);
				shop.setLatitude(latitude);
				shop.setLongitude(longitude);
				
				shops.add(shop);
			}
			return shops.toArray(new Shop[]{});
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}
	
	public Shop[] getAllShopWithOverall() throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETALLSHOPWITHOVERALL_SQL);
			ResultSet shopResSet = preStat.executeQuery();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + shopResSet.toString());
			}
			Map<Long, Shop> shops = new HashMap<Long, Shop>();
			while (shopResSet.next()) 
			{
				long id = shopResSet.getLong("shopId");
				Shop shop = shops.get(id);
				if (shop == null)
				{
					
					String eusername = shopResSet.getString("enterpriseUser");
					String type = shopResSet.getString("type");
					String title = shopResSet.getString("title");
					String content = shopResSet.getString("content");
					String shopImg = shopResSet.getString("shopImg");
					String district = shopResSet.getString("district");
					String street = shopResSet.getString("street");
					String tel = shopResSet.getString("tel");
					double longitude = shopResSet.getDouble("longitude");
					double latitude = shopResSet.getDouble("latitude");
					
					shop = new Shop();
					shop.setShopId(id);
					shop.setEusername(eusername);
					shop.setType(type);
					shop.setTitle(title);
					shop.setContent(content);
					shop.setShopImg(shopImg);
					shop.setDistrict(district);
					shop.setStreet(street);
					shop.setTel(tel);
					shop.setLatitude(latitude);
					shop.setLongitude(longitude);
					
					shops.put(shop.getShopId(), shop);
				}
				
				String itemName = shopResSet.getString("itemName");
				String itemValue = shopResSet.getString("itemValue");
				if (itemName != null && !shop.containOverall(itemName))
				{
					shop.addOverall(itemName, itemValue);
				}
				
				
				
			}
			return shops.values().toArray(new Shop[]{});
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}
	
	public Shop getShopDetail(long shopId) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETSHOPDETAIL_SQL);
			preStat.setLong(1, shopId);
			ResultSet shopResSet = preStat.executeQuery();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + shopResSet.toString());
			}
			Shop shop = null;
			if (shopResSet.next()) 
			{
				long id = shopResSet.getLong("shopId");
				if (shop == null)
				{
					shop = new Shop();
					String eusername = shopResSet.getString("enterpriseUser");
					String type = shopResSet.getString("type");
					String title = shopResSet.getString("title");
					String content = shopResSet.getString("content");
					String shopImg = shopResSet.getString("shopImg");
					String district = shopResSet.getString("district");
					String street = shopResSet.getString("street");
					String tel = shopResSet.getString("tel");
					double longitude = shopResSet.getDouble("longitude");
					double latitude = shopResSet.getDouble("latitude");

					shop.setShopId(id);
					shop.setEusername(eusername);
					shop.setType(type);
					shop.setTitle(title);
					shop.setContent(content);
					shop.setShopImg(shopImg);
					shop.setDistrict(district);
					shop.setStreet(street);
					shop.setTel(tel);
					shop.setLatitude(latitude);
					shop.setLongitude(longitude);
				}
				
				if (shop.getShopId() == id)
				{
					String itemName = shopResSet.getString("itemName");
					String itemValue = shopResSet.getString("itemValue");
					if (itemName != null && !shop.containOverall(itemName))
					{
						shop.addOverall(itemName, itemValue);
					}
				}
			}
			return shop;
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}
	
	public Object[] getShopByLoc(String shopType, int easting, int northing, int distance, int page, int count, boolean getTotal) throws Exception
	{
		Connection connection = null;
		Object[] returnValue = new Object[2];
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = null;
			if ("all".equals(shopType))
			{
				preStat = connection.prepareStatement(GETSHOPBYLOCWITHOUTTYPE_SQL);
				preStat.setInt(1, easting);
				preStat.setInt(2, northing);
				preStat.setInt(3, distance);
				preStat.setInt(4, (page - 1) * count);
				preStat.setInt(5, count);
			}
			else
			{
				preStat = connection.prepareStatement(GETSHOPBYLOC_SQL);
				preStat.setInt(1, easting);
				preStat.setInt(2, northing);
				preStat.setString(3, shopType);
				preStat.setInt(4, distance);
				preStat.setInt(5, (page - 1) * count);
				preStat.setInt(6, count);
			}
			
			
			ResultSet shopResSet = preStat.executeQuery();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + shopResSet.toString());
			}
			
			List<Shop> shops = new ArrayList<Shop>();
			while (shopResSet.next()) 
			{
				long id = shopResSet.getLong("shopId");
				String eusername = shopResSet.getString("enterpriseUser");
				String type = shopResSet.getString("type");
				String title = shopResSet.getString("title");
				String content = shopResSet.getString("content");
				String shopImg = shopResSet.getString("shopImg");
				String district = shopResSet.getString("district");
				String street = shopResSet.getString("street");
				String tel = shopResSet.getString("tel");
				double longitude = shopResSet.getDouble("longitude");
				double latitude = shopResSet.getDouble("latitude");
				double distanceFromUser = shopResSet.getDouble("distance");
				
				Shop shop = new Shop();
				shop.setShopId(id);
				shop.setEusername(eusername);
				shop.setType(type);
				shop.setTitle(title);
				shop.setContent(content);
				shop.setShopImg(shopImg);
				shop.setDistrict(district);
				shop.setStreet(street);
				shop.setTel(tel);
				shop.setLatitude(latitude);
				shop.setLongitude(longitude);
				shop.setDistanceFromUser(distanceFromUser);
				
				shops.add(shop);
			}
			returnValue[1] = shops.toArray(new Shop[]{});
			
			if (getTotal)
			{
				connection = connectionPool.getConnection();
				PreparedStatement preStat2 = null;
				if ("all".equals(shopType))
				{
					preStat2 = connection.prepareStatement(GETSHOPBYLOCCOUNTWITHOUTTYPE_SQL);
					preStat2.setInt(1, easting);
					preStat2.setInt(2, northing);
					preStat2.setInt(3, distance);
				}
				else
				{
					preStat2 = connection.prepareStatement(GETSHOPBYLOCCOUNT_SQL);
					preStat2.setInt(1, easting);
					preStat2.setInt(2, northing);
					preStat2.setString(3, shopType);
					preStat2.setInt(4, distance);
				}
				ResultSet shopResSet2 = preStat2.executeQuery();
				if (loggerServiceTracker.isDebugEnabled())
				{
					loggerServiceTracker.debug("SQL:" + preStat2.toString());
					loggerServiceTracker.debug("Result:" + shopResSet2.toString());
				}
				if (shopResSet2.next()) 
				{
					int countResult = shopResSet2.getInt("COUNT(*)");
					returnValue[0] = countResult;
				}
				
				
			}
			
			return returnValue;
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}
	
	public Object[] getShopByKey(String shopType, String key, int page, int count, boolean getTotal) throws Exception
	{
		Connection connection = null;
		Object[] returnValue = new Object[2];
		try
		{
			String search = "%" + key + "%";
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETSHOPBYKEY_SQL);
			preStat.setString(1, search);
			preStat.setString(2, search);
			preStat.setString(3, search);
			preStat.setString(4, search);
			preStat.setInt(5, (page - 1) * count);
			preStat.setInt(6, count);
			ResultSet shopResSet = preStat.executeQuery();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + shopResSet.toString());
			}
			
			List<Shop> shops = new ArrayList<Shop>();
			while (shopResSet.next()) 
			{
				long id = shopResSet.getLong("shopId");
				String eusername = shopResSet.getString("enterpriseUser");
				String type = shopResSet.getString("type");
				String title = shopResSet.getString("title");
				String content = shopResSet.getString("content");
				String shopImg = shopResSet.getString("shopImg");
				String district = shopResSet.getString("district");
				String street = shopResSet.getString("street");
				String tel = shopResSet.getString("tel");
				double longitude = shopResSet.getDouble("longitude");
				double latitude = shopResSet.getDouble("latitude");
				
				Shop shop = new Shop();
				shop.setShopId(id);
				shop.setEusername(eusername);
				shop.setType(type);
				shop.setTitle(title);
				shop.setContent(content);
				shop.setShopImg(shopImg);
				shop.setDistrict(district);
				shop.setStreet(street);
				shop.setTel(tel);
				shop.setLatitude(latitude);
				shop.setLongitude(longitude);
				
				shops.add(shop);
			}
			returnValue[1] = shops.toArray(new Shop[]{});
			
			if (getTotal)
			{
				PreparedStatement preStat2 = connection.prepareStatement(GETSHOPBYKEYCOUNT_SQL);
				preStat2.setString(1, search);
				preStat2.setString(2, search);
				preStat2.setString(3, search);
				preStat2.setString(4, search);
				ResultSet shopResSet2 = preStat2.executeQuery();
				if (loggerServiceTracker.isDebugEnabled())
				{
					loggerServiceTracker.debug("SQL:" + preStat2.toString());
					loggerServiceTracker.debug("Result:" + shopResSet2.toString());
				}
				if (shopResSet2.next()) 
				{
					int countResult = shopResSet2.getInt("COUNT(*)");
					returnValue[0] = countResult;
				}
			}
			
			
			
			return returnValue;
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}
	
	public void addComment(ShopComment shopComment, List<ShopVoter> voters) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			connection.setAutoCommit(false);
			
			PreparedStatement preStat = connection.prepareStatement(ADDSHOPCOMMENT_SQL);
			preStat.setLong(1, shopComment.getShopId());
			preStat.setString(2, shopComment.getUsername());
			preStat.setInt(3, shopComment.getScore());
			preStat.setString(4, shopComment.getContent());
			
			preStat.executeUpdate();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
			}
			for (ShopVoter voter : voters)
			{
				PreparedStatement preStat2 = connection.prepareStatement(ADDSHOPVOTER_SQL);
				preStat2.setString(1, voter.getUsername());
				preStat2.setLong(2, voter.getShopId());
				preStat2.setString(3, voter.getItemName());
				preStat2.setInt(4, voter.getValue());
				preStat2.executeUpdate();
				if (loggerServiceTracker.isDebugEnabled())
				{
					loggerServiceTracker.debug("SQL:" + preStat2.toString());
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
	
	public ShopComment[] getShopComments(long shopId, int page, int count) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETSHOPCOOMENTS_SQL);
			preStat.setLong(1, shopId);
			preStat.setInt(2, (page - 1) * count);
			preStat.setInt(3, count);
			ResultSet commentsResSet = preStat.executeQuery();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + commentsResSet.toString());
			}
			
			List<ShopComment> comments = new ArrayList<ShopComment>();
			
			while (commentsResSet.next()) 
			{
				ShopComment comment = new ShopComment();
				comment.setShopId(commentsResSet.getLong("shopId"));
				comment.setCommentId(commentsResSet.getLong("commentId"));
				comment.setContent(commentsResSet.getString("content"));
				comment.setUsername(commentsResSet.getString("username"));
				comment.setScore(commentsResSet.getInt("score"));
				Timestamp timestamp = commentsResSet.getTimestamp("modificationDate");
				comment.setLasModitDate(timestamp.getTime());
				
				comments.add(comment);
			}
			return comments.toArray(new ShopComment[]{});
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}
	
	public Object[] getUserShopComments(String username, int page, int count, boolean getTotal) throws Exception
	{
		Connection connection = null;
		Object[] returnValue = new Object[2];
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETUSERSHOPCOOMENTS_SQL);
			preStat.setString(1, username);
			preStat.setInt(2, (page - 1) * count);
			preStat.setInt(3, count);
			ResultSet commentsResSet = preStat.executeQuery();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + commentsResSet.toString());
			}
			List<ShopComment> comments = new ArrayList<ShopComment>();
			
			while (commentsResSet.next()) 
			{
				ShopComment comment = new ShopComment();
				comment.setShopId(commentsResSet.getLong("shopId"));
				comment.setContent(commentsResSet.getString("content"));
				comment.setUsername(username);
				comment.setScore(commentsResSet.getInt("score"));
				Timestamp timestamp = commentsResSet.getTimestamp("modificationDate");
				comment.setLasModitDate(timestamp.getTime());
				
				comment.setProperty("shopTitle", commentsResSet.getString("title"));
				comments.add(comment);
			}
			returnValue[1] = comments.toArray(new ShopComment[]{});
			
			if (getTotal)
			{
				PreparedStatement preStat2 = connection.prepareStatement(GETUSERSHOPCOOMENTSCOUNT_SQL);
				preStat2.setString(1, username);
				ResultSet shopResSet2 = preStat2.executeQuery();
				if (loggerServiceTracker.isDebugEnabled())
				{
					loggerServiceTracker.debug("SQL:" + preStat2.toString());
					loggerServiceTracker.debug("Result:" + shopResSet2.toString());
				}
				if (shopResSet2.next()) 
				{
					int countResult = shopResSet2.getInt("COUNT(*)");
					returnValue[0] = countResult;
				}
			}
			
			
			return returnValue;
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}
	

	public Object[] getFavoriteShop(String username, int page, int count, boolean getTotal) throws Exception
	{
		Connection connection = null;
		Object[] result = new Object[2];
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETFAVORITESHOP_SQL);
			preStat.setString(1, username);
			preStat.setInt(2, (page - 1) * count);
			preStat.setInt(3, count);
			ResultSet resultSet = preStat.executeQuery();
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + resultSet.toString());
			}
			List<UserFavoriteShop> entities = new ArrayList<UserFavoriteShop>();
			while (resultSet.next())
			{
//				String username2 = resultSet.getString("username");
				long id = resultSet.getLong("id");
				long shopId = resultSet.getLong("shopId");
				String name = resultSet.getString("title");
				String street = resultSet.getString("street");
				String tel = resultSet.getString("tel");
				
				UserFavoriteShop entity = new UserFavoriteShop();
				entity.setId(id);
				entity.setShopId(shopId);
				entity.setUsername(username);
				entity.setShopName(name);
				entity.setStreet(street);
				entity.setTel(tel);
				
				entities.add(entity);
			}
			result[1] = entities.toArray(new UserFavoriteShop[]{});

			if (getTotal)
			{
				PreparedStatement preStat2 = connection.prepareStatement(GETCOUNTOFFAVORITESHOP_SQL);
				preStat2.setString(1, username);
				ResultSet resultSet2 = preStat2.executeQuery();
				if (loggerServiceTracker.isDebugEnabled())
				{
					loggerServiceTracker.debug("SQL:" + preStat2.toString());
					loggerServiceTracker.debug("Result:" + resultSet2.toString());
				}
				if (resultSet2.next())
				{
					int resultCount = resultSet2.getInt("COUNT(*)");
					result[0] = resultCount;
				}
			}
			
			return result;
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
		}

		
	}
	
	public void addFavoriteShop(String username, long shopId) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(ADDFAVORITESHOP_SQL);
			preStat.setString(1, username);
			preStat.setLong(2, shopId);
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
	
	public boolean containFavoriteShop(String username, long shopId) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(CONTAINFAVORITESHOP_SQL);
			preStat.setString(1, username);
			preStat.setLong(2, shopId);
			ResultSet rs = preStat.executeQuery();
			
			if (loggerServiceTracker.isDebugEnabled())
			{
				loggerServiceTracker.debug("SQL:" + preStat.toString());
				loggerServiceTracker.debug("Result:" + preStat.toString());
			}
			
			return rs.next();
		}
		finally
		{
			if (connection != null)
			{
				connectionPool.returnConnection(connection);
			}
			
		}
	}
	
	public void removeFavoriteShop(String username, long favoriteshopid) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(REMOVEFAVORITESHOP_SQL);
			preStat.setString(1, username);
			preStat.setLong(2, favoriteshopid);
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
