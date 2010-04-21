package com.google.code.christy.shopactivityservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
			handleSearchNear(req, resp);
		}
		else if ("getshopdetail".equals(action))
		{
			handleGetShopDetail(req, resp);
		}
	}

	private void handleGetShopDetail(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String shopId = req.getParameter("shopid");
		JSONObject jsonObj = new JSONObject();
		
		try
		{
			JSONObject basicInfo = new JSONObject();
			basicInfo.put("id", shopId);
			basicInfo.put("name", "上海1号私藏菜");
			basicInfo.put("imgSrc", "/resource/hongshaorou.jpg");
			basicInfo.put("hasCoupon", true);
			basicInfo.put("score", 90);
			basicInfo.put("perCapita", 50);
			basicInfo.put("addr", "上海市静安区南京西路1856号");
			basicInfo.put("phone", 123456789);
			jsonObj.put("basicInfo", basicInfo);
			
			jsonObj.put("intro", "私藏菜比私房菜更多一点点“藏”的意思，有“酒香不怕巷子深”的傲气，正合了中国人爱追根究底的惯常。" +
					"所以对于私藏变为公众皆知的秘密也就理所当然，无数的欲说还休。老上海的韵味一边敛一边放。老式台灯、" +
					"桌案、杨州漆器、铁质鸟笼、欧式沙发、回纹走廊等等，尽数着婉约复古的气息，美食暖胃，缓如流水。" +
					"上海1号私藏菜是以本帮菜、海派菜为主打，每一道菜都是玩过花样儿的。即使冠着简单寻常的名字，" +
					"厨师们却下了无数的心思在里面，让时尚上海人的健康饮食观念贯彻得更透，浓油赤酱皆改作了清爽耐品，" +
					"许多烹饪秘方私家独创，精致耐品，故名之“私藏菜”。细碟精巧的手撕豇豆藏着淡淡芥末味，毫无疑问地手工制作；" +
					"老弄堂红烧肉的选材更是讲究，只用野猪与家猪杂交的第五代猪肉；火山石器烧裙翅用功深，滋补功效好，中看中吃");
			
			
			
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
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleSearchNear(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String latitudeStr = req.getParameter("latitude");
		String longitudeStr = req.getParameter("longitude");
		
		double latitude = Double.parseDouble(latitudeStr);
		double longitude = Double.parseDouble(longitudeStr);
		
		try
		{
			Shop[] shops = shopDbhelper.getAllShop();
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
					jsonObj.put("hasCoupon", true);
					jsonObj.put("score", 90);
					jsonObj.put("perCapita", 50);
					jsonObj.put("street", s.getStreet());
					jsonObj.put("type", s.getType());
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
