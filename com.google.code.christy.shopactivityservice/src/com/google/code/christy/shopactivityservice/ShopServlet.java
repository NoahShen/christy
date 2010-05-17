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

	private ShopDbhelper shopDbhelper;
	
	public ShopServlet(LoggerServiceTracker loggerServiceTracker, ShopDbhelper shopLocDbhelper)
	{
		super();
		this.loggerServiceTracker = loggerServiceTracker;
		this.shopDbhelper = shopLocDbhelper;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		loggerServiceTracker.debug("doGet");
		
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
			
			
			JSONObject overall = new JSONObject();
			for (Map.Entry<String, String> entry : shop.getShopOverall().entrySet())
			{
				overall.put(entry.getKey(), entry.getValue());
			}
			jsonObj.put("overall", overall);
			
			basicInfo.put("addr", shop.getDistrict() + " " + shop.getStreet());
			basicInfo.put("phone", shop.getTel());
			jsonObj.put("basicInfo", basicInfo);
			
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
		String eastingStr = req.getParameter("easting");
		String northingStr = req.getParameter("northing");
		
		String shopType = req.getParameter("type");
		
		String pageStr = req.getParameter("page");
		String countStr = req.getParameter("count");
		
		int page = pageStr == null ?  1 : Integer.parseInt(pageStr);
		int count = countStr == null ?  10 : Integer.parseInt(countStr);
		
		int easting = Integer.parseInt(eastingStr);
		int northing = Integer.parseInt(northingStr);
		
		try
		{
			Object[] result = shopDbhelper.getShopByLoc(shopType, easting, northing, 10, page, count);
			int resultCount = (Integer) result[0];
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
					array.put(jsonObj);
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			resultJsonObj.put("total", resultCount);
			resultJsonObj.put("shops", array);
			resp.getWriter().write(resultJsonObj.toString());
			
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		loggerServiceTracker.debug("doPost");
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
