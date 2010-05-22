package com.google.code.christy.shopactivityservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.code.christy.log.LoggerServiceTracker;

public class ShopServlet extends HttpServlet
{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1279166265755384334L;

	private LoggerServiceTracker loggerServiceTracker;
	
	private C2SManagerTracker c2sManagerTracker;
	
	private ShopDbhelper shopDbhelper;
	
	private UserDbhelper userDbhelper;
	
	public ShopServlet(C2SManagerTracker c2sManagerTracker, 
				LoggerServiceTracker loggerServiceTracker, 
				ShopDbhelper shopLocDbhelper,
				UserDbhelper userDbhelper)
	{
		super();
		this.c2sManagerTracker = c2sManagerTracker;
		this.loggerServiceTracker = loggerServiceTracker;
		this.shopDbhelper = shopLocDbhelper;
		this.userDbhelper = userDbhelper;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		loggerServiceTracker.debug("doPost");
		
		req.setCharacterEncoding("UTF-8");
		
		resp.setContentType("text/json;charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		String action = req.getParameter("action");
		if ("search".equals(action))
		{
			handleSearch(req, resp);
		}
		else if ("getshopdetail".equals(action))
		{
			handleGetShopDetail(req, resp);
		}
		else if ("submitShopComment".equals(action))
		{
			handleSubmitComment(req, resp);
		}
		else if ("getshopcomments".equals(action))
		{
			handleGetShopcomments(req, resp);
		}
		else if ("getusershopcomments".equals(action))
		{
			handleGetUserShopComments(req, resp);
		}
		else if ("getfavoriteshops".equals(action))
		{
			handleGetFavoriteShops(req, resp);
		}
		else if ("removefavoriteshop".equals(action))
		{
			handleRemoveFavoriteShop(req, resp);
		}
		else if ("addfavoriteshop".equals(action))
		{
			handleAddFavoriteShop(req, resp);
		}
		else if ("register".equals(action))
		{
			handleRegister(req, resp);
		}
	}

	private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
	{
		String username = req.getParameter("username").toLowerCase();
		String password = req.getParameter("password");
		String email = req.getParameter("email").toLowerCase();
		
		try
		{
			int result = userDbhelper.contain(username, email);
			if (result == 1) 
			{
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("result", "failed");
				jsonObj.put("reason", "usernameDuplicated");
				resp.getWriter().write(jsonObj.toString());
				return;
			}
			
			if (result == 2) 
			{
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("result", "failed");
				jsonObj.put("reason", "emailDuplicated");
				resp.getWriter().write(jsonObj.toString());
				return;
			}
			
			
			userDbhelper.addUser(username, password, email);
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("result", "success");
			resp.getWriter().write(jsonObj.toString());
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleAddFavoriteShop(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String streamId = req.getParameter("streamid");
		
		if (!c2sManagerTracker.containStreamId(streamId))
		{
			resp.setContentType("text/html;charset=UTF-8");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "invalided streamId");
			return;
		}
		String username = req.getParameter("username").toLowerCase();
		String shopId = req.getParameter("shopid");
		try
		{
			shopDbhelper.addFavoriteShop(username, Long.parseLong(shopId));
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("result", "success");
			resp.getWriter().write(jsonObj.toString());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleRemoveFavoriteShop(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String streamId = req.getParameter("streamid");
		
		if (!c2sManagerTracker.containStreamId(streamId))
		{
			resp.setContentType("text/html;charset=UTF-8");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "invalided streamId");
			return;
		}
		
		String username = req.getParameter("username").toLowerCase();
		String favoriteshopid = req.getParameter("favoriteshopid");
		try
		{
			shopDbhelper.removeFavoriteShop(username, Long.parseLong(favoriteshopid));
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("result", "success");
			resp.getWriter().write(jsonObj.toString());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleGetFavoriteShops(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String streamId = req.getParameter("streamid");
		
		if (!c2sManagerTracker.containStreamId(streamId))
		{
			resp.setContentType("text/html;charset=UTF-8");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "invalided streamId");
			return;
		}
		
		String username = req.getParameter("username").toLowerCase();
		
		String pageStr = req.getParameter("page");
		String countStr = req.getParameter("count");
		
		int page = pageStr == null ?  1 : Integer.parseInt(pageStr);
		int count = countStr == null ?  10 : Integer.parseInt(countStr);
		
		String getTotalStr = req.getParameter("gettotal");
		boolean getTotal = false;
		if (getTotalStr != null)
		{
			getTotal = true;
		}
		
		try
		{
			Object[] result = shopDbhelper.getFavoriteShop(username, page, count, getTotal);
			Integer resultCount = (Integer) result[0];
			UserFavoriteShop favoriteShops[] = (UserFavoriteShop[]) result[1];
			
			JSONArray commentsJson = new JSONArray();
			
			for (UserFavoriteShop favoriteShop : favoriteShops)
			{
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("favoriteShopId", favoriteShop.getId());
				jsonObj.put("shopId", favoriteShop.getShopId());
				jsonObj.put("shopName", favoriteShop.getShopName());
				jsonObj.put("street", favoriteShop.getStreet());
				commentsJson.put(jsonObj);
			}
			
			JSONObject jsonObj = new JSONObject();
			if (resultCount != null)
			{
				jsonObj.put("total", resultCount);
			}
			
			jsonObj.put("shops", commentsJson);
			resp.getWriter().write(jsonObj.toString());
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	private void handleGetUserShopComments(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String streamId = req.getParameter("streamid");
		
		if (!c2sManagerTracker.containStreamId(streamId))
		{
			resp.setContentType("text/html;charset=UTF-8");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "invalided streamId");
			return;
		}
		
		String username = req.getParameter("username").toLowerCase();
		
		String pageStr = req.getParameter("page");
		String countStr = req.getParameter("count");
		
		int page = pageStr == null ?  1 : Integer.parseInt(pageStr);
		int count = countStr == null ?  10 : Integer.parseInt(countStr);
		
		String getTotalStr = req.getParameter("gettotal");
		boolean getTotal = false;
		if (getTotalStr != null)
		{
			getTotal = true;
		}
		try
		{
			Object[] result = shopDbhelper.getUserShopComments(username, page, count, getTotal);
			Integer resultCount = (Integer) result[0];
			ShopComment comments[] = (ShopComment[]) result[1];
			
			JSONArray commentsJson = new JSONArray();
			
			for (ShopComment comment : comments)
			{
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("shopId", comment.getShopId());
				jsonObj.put("shopTitle", comment.getProperty("shopTitle"));
				jsonObj.put("score", comment.getScore());
				jsonObj.put("time", comment.getLasModitDate());
				jsonObj.put("content", comment.getContent());
				commentsJson.put(jsonObj);
			}
			
			JSONObject jsonObj = new JSONObject();
			if (resultCount != null)
			{
				jsonObj.put("total", resultCount);
			}
			jsonObj.put("comments", commentsJson);
			resp.getWriter().write(jsonObj.toString());
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	private void handleGetShopcomments(HttpServletRequest req, HttpServletResponse resp)
	{
		String shopId = req.getParameter("shopid");
		
		String pageStr = req.getParameter("page");
		String countStr = req.getParameter("count");
		
		int page = pageStr == null ?  1 : Integer.parseInt(pageStr);
		int count = countStr == null ?  10 : Integer.parseInt(countStr);
		
		try
		{
			ShopComment comments[] = shopDbhelper.getShopComments(Long.parseLong(shopId), page, count);
			
			JSONArray commentsJson = new JSONArray();
			
			for (ShopComment comment : comments)
			{
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("username", comment.getUsername());
				jsonObj.put("score", comment.getScore());
				jsonObj.put("time", comment.getLasModitDate());
				jsonObj.put("content", comment.getContent());
				commentsJson.put(jsonObj);
			}
			
			resp.getWriter().write(commentsJson.toString());
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void handleSubmitComment(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String username = req.getParameter("username");
		String shopIdStr = req.getParameter("shopid");
		long shopId = Long.parseLong(shopIdStr);
		String commentContent = req.getParameter("comentContent");
		
		String[] commentIems = commentContent.split(";");
		
		List<ShopVoter> voters = new ArrayList<ShopVoter>();
		ShopComment shopComment = new ShopComment();
		shopComment.setShopId(shopId);
		shopComment.setUsername(username);
		for (String commentItem : commentIems)
		{
			String[] itemNameValue = commentItem.split(":");
			String itemName = itemNameValue[0];
			String itemValue = itemNameValue[1];
			if ("score".equals(itemName))
			{
				shopComment.setScore(Integer.parseInt(itemValue));
			}
			else if ("content".equals(itemName))
			{
				shopComment.setContent(itemValue);
			}
			else
			{
				ShopVoter voter = new ShopVoter();
				voter.setShopId(shopId);
				voter.setUsername(username);
				voter.setItemName(itemName);
				voter.setValue(Integer.parseInt(itemValue));
				voters.add(voter);
			}
		}
		
		try
		{
			shopDbhelper.addComment(shopComment, voters);
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		JSONObject jsonObj = new JSONObject();
		try
		{
			jsonObj.put("result", "success");
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resp.getWriter().write(jsonObj.toString());
	}

	private void handleGetShopDetail(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String shopId = req.getParameter("shopid");
		
		try
		{
			Shop shop = shopDbhelper.getShopDetail(Long.parseLong(shopId));
			JSONObject jsonObj = new JSONObject();
			
			JSONObject basicInfo = new JSONObject();
			basicInfo.put("id", shopId);
			basicInfo.put("name", shop.getTitle());
			basicInfo.put("imgSrc", shop.getShopImg());
			basicInfo.put("hasCoupon", true);
			basicInfo.put("addr", shop.getDistrict() + " " + shop.getStreet());
			basicInfo.put("phone", shop.getTel());
			basicInfo.put("lat", shop.getLatitude());
			basicInfo.put("lon", shop.getLongitude());
			jsonObj.put("basicInfo", basicInfo);
			
			
			JSONObject overall = new JSONObject();
			for (Map.Entry<String, String> entry : shop.getShopOverall().entrySet())
			{
				overall.put(entry.getKey(), entry.getValue());
			}
			jsonObj.put("overall", overall);
			
			
			jsonObj.put("intro", shop.getContent());
			
			resp.getWriter().write(jsonObj.toString());
				
			
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	private void handleSearch(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String searchKey = req.getParameter("searchKey");
		String eastingStr = req.getParameter("easting");
		String northingStr = req.getParameter("northing");
		
		Object[] result = null;
		if (searchKey != null)
		{		
			result = searchByKey(req, resp, searchKey);
		} 
		else if (eastingStr != null && northingStr != null)
		{
			result = searchByLoc(req, resp, Integer.parseInt(eastingStr), Integer.parseInt(northingStr));
		}
		
		
		if (result != null)
		{
			Integer resultCount = (Integer) result[0];
			Shop[] resultShops = (Shop[]) result[1];
			
			JSONObject resultJsonObj = new JSONObject();
			JSONArray array = new JSONArray();
			for (Shop s : resultShops)
			{
				JSONObject jsonObj = new JSONObject();
				try
				{
					jsonObj.put("id", s.getShopId());
					jsonObj.put("name", s.getTitle());
					jsonObj.put("imgSrc", s.getShopImg());
					jsonObj.put("tel", s.getTel());
					jsonObj.put("street", s.getStreet());
					if (s.getDistanceFromUser() != null)
					{
						jsonObj.put("distance", s.getDistanceFromUser().doubleValue());
					}
					array.put(jsonObj);
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			try
			{
				if (resultCount != null)
				{
					resultJsonObj.put("total", resultCount);
				}
				resultJsonObj.put("shops", array);
				resp.getWriter().write(resultJsonObj.toString());
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	private Object[] searchByLoc(HttpServletRequest req, HttpServletResponse resp, int easting, int northing)
	{

		String shopType = req.getParameter("type");
		
		String pageStr = req.getParameter("page");
		String countStr = req.getParameter("count");
		
		int page = pageStr == null ?  1 : Integer.parseInt(pageStr);
		int count = countStr == null ?  10 : Integer.parseInt(countStr);
		
		String distanceStr = req.getParameter("distance");
		int distance = Integer.parseInt(distanceStr);
		
		String getTotalStr = req.getParameter("gettotal");
		boolean getTotal = false;
		if (getTotalStr != null)
		{
			getTotal = true;
		}
		try
		{
			Object[] result = shopDbhelper.getShopByLoc(shopType, easting, northing, distance, page, count, getTotal);
			return result;
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
		
	}

	private Object[] searchByKey(HttpServletRequest req, HttpServletResponse resp, String searchKey)
	{
		String shopType = req.getParameter("type");
		
		String pageStr = req.getParameter("page");
		String countStr = req.getParameter("count");
		
		int page = pageStr == null ?  1 : Integer.parseInt(pageStr);
		int count = countStr == null ?  10 : Integer.parseInt(countStr);
		
		String getTotalStr = req.getParameter("gettotal");
		boolean getTotal = false;
		if (getTotalStr != null)
		{
			getTotal = true;
		}
		try
		{
			Object[] result = shopDbhelper.getShopByKey(shopType, searchKey, page, count, getTotal);
			return result;
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		loggerServiceTracker.debug("doGet");
		doPost(req, resp);
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	public void init() throws ServletException
	{
		// TODO Auto-generated method stub
		super.init();
	}

}
