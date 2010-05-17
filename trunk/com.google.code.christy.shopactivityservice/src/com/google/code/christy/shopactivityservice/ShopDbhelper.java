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
						" FROM shop WHERE type= ? HAVING distance<=(? * 1000) ORDER BY distance LIMIT ?, ?";
	
	private static final String GETSHOPBYLOCCOUNT_SQL = "SELECT COUNT(*) FROM (SELECT shopId, SQRT(POW(? - easting, 2) + POW(? - northing, 2)) AS distance FROM shop WHERE type= ? HAVING distance<=(? * 1000)) R";
	
	private static final String GETSHOPCOOMENTS_SQL = "SELECT *  FROM shopcomment WHERE shopId = ? ORDER BY modificationDate DESC LIMIT ?, ?";
	
	public ShopDbhelper(ConnectionPool connectionPool)
	{
		super();
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

			Shop shop = null;
			while (shopResSet.next()) 
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
//					
//					Integer commentId = (Integer) shopResSet.getObject("commentId");
//					if (commentId != null && !shop.containComment(commentId.longValue()))
//					{
//						String username = shopResSet.getString("username");
//						int score = shopResSet.getInt("score");
//						String commentContent = shopResSet.getString("commentContent");
//						Timestamp timestamp = shopResSet.getTimestamp("commentMoDate");
//						
//						ShopComment comment = new ShopComment();
//						comment.setCommentId(commentId.longValue());
//						comment.setContent(commentContent);
//						comment.setLasModitDate(timestamp.getTime());
//						comment.setScore(score);
//						comment.setUsername(username);
//						comment.setShopId(id);
//						shop.addComment(comment);
//					}
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
	
	public Object[] getShopByLoc(String shopType, int easting, int northing, int distance, int page, int count) throws Exception
	{
		Connection connection = null;
		Object[] returnValue = new Object[2];
		try
		{
			connection = connectionPool.getConnection();
			PreparedStatement preStat = connection.prepareStatement(GETSHOPBYLOC_SQL);
			preStat.setInt(1, easting);
			preStat.setInt(2, northing);
			preStat.setString(3, shopType);
			preStat.setInt(4, distance);
			preStat.setInt(5, (page - 1) * count);
			preStat.setInt(6, count);
			ResultSet shopResSet = preStat.executeQuery();
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
			
			PreparedStatement preStat2 = connection.prepareStatement(GETSHOPBYLOCCOUNT_SQL);
			preStat2.setInt(1, easting);
			preStat2.setInt(2, northing);
			preStat2.setString(3, shopType);
			preStat2.setInt(4, distance);
			ResultSet shopResSet2 = preStat2.executeQuery();
			if (shopResSet2.next()) 
			{
				int countResult = shopResSet2.getInt("COUNT(*)");
				returnValue[0] = countResult;
			}
			
			returnValue[1] = shops.toArray(new Shop[]{});
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
			
			for (ShopVoter voter : voters)
			{
				PreparedStatement preStat2 = connection.prepareStatement(ADDSHOPVOTER_SQL);
				preStat2.setString(1, voter.getUsername());
				preStat2.setLong(2, voter.getShopId());
				preStat2.setString(3, voter.getItemName());
				preStat2.setInt(4, voter.getValue());
				preStat2.executeUpdate();
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
}
