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
			handleSearchNearby(req, resp);
		}
		else if ("getshopdetail".equals(action))
		{
			handleGetShopDetail(req, resp);
		}
	}

	private void handleGetShopDetail(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String shopId = req.getParameter("shopid");
		
		try
		{
			Shop shop = shopDbhelper.getShop(Long.parseLong(shopId));
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
			
			
			
			JSONArray evaluations = new JSONArray();
			JSONObject evaluation1 = new JSONObject();
			evaluation1.put("name", "服务");
			evaluation1.put("value", 90);
			evaluations.put(evaluation1);
			
			JSONObject evaluation2 = new JSONObject();
			evaluation2.put("name", "口味");
			evaluation2.put("value", 91);			
			evaluations.put(evaluation2);
			
			jsonObj.put("evaluation", evaluations);
			
			JSONArray comments = new JSONArray();
			JSONObject comment1 = new JSONObject();
			comment1.put("username", "Noah");
			comment1.put("time", System.currentTimeMillis());
			comment1.put("score", 90);
			comment1.put("content", "早就听说这家的菜很好吃了 很多人都喜欢来这家的哦 我是和家人一起来的 那次我们吃的都是很满意呢 这家的环境还是不错哦 价格也是公道的 我们都是可以接受呢 真的是不错哦 热情的服务也是我们非常的满意呢 值得来试试哦");
			comments.put(comment1);

			JSONObject comment2 = new JSONObject();
			comment2.put("username", "Noah2");
			comment2.put("time", System.currentTimeMillis());
			comment2.put("score", 90);
			comment2.put("content", "很奇怪的一家店，11点过去，刚开门的时候，居然就排队，排队的都是5、60的老人。诺大的店堂，居然只有非常小的电梯可以上去，一次也就6个人。中午的午市的火山石器烧裙翅吃口不错，才43，的确是特色了。下次有机会来吃点心");
			comments.put(comment2);
			
			jsonObj.put("comments", comments);
			
			resp.getWriter().write(jsonObj.toString());
				
			
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	private void handleSearchNearby(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String latitudeStr = req.getParameter("latitude");
		String longitudeStr = req.getParameter("longitude");
		
		double latitude = Double.parseDouble(latitudeStr);
		double longitude = Double.parseDouble(longitudeStr);
		
		try
		{
			Shop[] shops = shopDbhelper.getAllShopWithOverall();
			List<Shop> nearShops = new ArrayList<Shop>();
			for (Shop shop : shops)
			{
				
				double distance = GeoUtils.distanceOfTwoPoints(longitude, latitude, 
									shop.getLongitude(), shop.getLatitude(),
									GeoUtils.GaussSphere.WGS84);

				if (distance <= 10 * 1000)
				{
					nearShops.add(shop);
				}
			}
			
			JSONArray array = new JSONArray();
			for (Shop s : nearShops)
			{
				JSONObject jsonObj = new JSONObject();
				try
				{
					jsonObj.put("id", s.getShopId());
					jsonObj.put("name", s.getTitle());
					jsonObj.put("imgSrc", s.getShopImg());
					jsonObj.put("hasCoupon", false);
					
					JSONObject overall = new JSONObject();
					for (Map.Entry<String, String> entry : s.getShopOverall().entrySet())
					{
						overall.put(entry.getKey(), entry.getValue());
					}
					jsonObj.put("overall", overall);
					
					
					jsonObj.put("street", s.getStreet());
					array.put(jsonObj);
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			
			resp.getWriter().write(array.toString());
			
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
