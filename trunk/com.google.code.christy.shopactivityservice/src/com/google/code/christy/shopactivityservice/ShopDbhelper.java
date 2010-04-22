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
							" O.itemName, O.itemValue," +
							" C.commentId, C.username, C.score, C.content AS commentContent, C.modificationDate as commentMoDate FROM" +
							" (SELECT * FROM shop WHERE shopId = ?) R" +
							" LEFT JOIN shopoverall O ON O.shopId = R.shopId" +
							" LEFT JOIN shopcomment C ON C.shopId = R.shopId" +
							" ORDER BY C.modificationDate DESC";

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
					
					shops.put(id, shop);
				}
				
				String itemName = shopResSet.getString("itemName");
				String itemValue = shopResSet.getString("itemValue");
				if (itemName != null)
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
					
					long commentId = shopResSet.getLong("commentId");
					if (!shop.containComment(commentId))
					{
						String username = shopResSet.getString("username");
						int score = shopResSet.getInt("score");
						String commentContent = shopResSet.getString("commentContent");
						Timestamp timestamp = shopResSet.getTimestamp("commentMoDate");
						
						ShopComment comment = new ShopComment();
						comment.setCommentId(commentId);
						comment.setContent(commentContent);
						comment.setLasModitDate(timestamp.getTime());
						comment.setScore(score);
						comment.setUsername(username);
						comment.setShopId(id);
						shop.addComment(comment);
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
}
