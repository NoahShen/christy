package com.google.code.christy.shopactivityservice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.code.christy.cache.Cache;
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

	private Seacher seacher;
	
	private CacheServiceTracker cacheServiceTracker;
	
	public ShopServlet(C2SManagerTracker c2sManagerTracker, 
				LoggerServiceTracker loggerServiceTracker, 
				ShopDbhelper shopLocDbhelper,
				UserDbhelper userDbhelper,
				CacheServiceTracker cacheServiceTracker)
	{
		super();
		this.c2sManagerTracker = c2sManagerTracker;
		this.loggerServiceTracker = loggerServiceTracker;
		this.shopDbhelper = shopLocDbhelper;
		this.userDbhelper = userDbhelper;
		this.cacheServiceTracker = cacheServiceTracker;
		
		this.seacher = new Seacher();
		try
		{
			this.seacher.createIndex();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
				jsonObj.put("tel", favoriteShop.getTel());
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
			Cache cache = cacheServiceTracker.getCache("shopcommentsCache");
			String key = shopId + "-" + page + "-" + count;
			ShopComment[] comments = (ShopComment[]) cache.get(key);
			
			if (comments == null)
			{
				comments = shopDbhelper.getShopComments(Long.parseLong(shopId), page, count);
				cache.put(key, comments);
				
				String commentsCacheMarker = shopId + "CommentCache";
				String markerValue = (String) cache.get(commentsCacheMarker);
				if (markerValue == null)
				{
					markerValue = "";
				}
				
				StringBuilder sbuilder = new StringBuilder(markerValue);
				sbuilder.append(key + ";");
				cache.put(shopId + "CommentCache", sbuilder.toString());
			}
			
			
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
			
			Cache cache = cacheServiceTracker.getCache("shopcommentsCache");
			
			String commentsCacheMarker = shopId + "CommentCache";
			String markerValue = (String) cache.get(commentsCacheMarker);
			if (markerValue != null)
			{
				String[] caches = markerValue.split(";");
				for (String cacheKey : caches)
				{
					if (cacheKey != null && !cacheKey.isEmpty())
					{
						cache.remove(cacheKey);
					}
					
				}
				cache.remove(commentsCacheMarker);
			}
			
			
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
			Cache cache = cacheServiceTracker.getCache("shopDetailCache");
			Shop shop = (Shop) cache.get(shopId);
			if (shop == null)
			{
				shop = shopDbhelper.getShopDetail(Long.parseLong(shopId));
				cache.put(shopId, shop);
			}
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
			basicInfo.put("type", shop.getType());
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
//		String shopType = req.getParameter("type");
		
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
//		try
//		{
//			Object[] result = shopDbhelper.getShopByKey(shopType, searchKey, page, count, getTotal);
//			return result;
//		}
//		catch (Exception e1)
//		{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		try
		{
			return this.seacher.seachIndex(searchKey, page, count, getTotal);
		}
		catch (CorruptIndexException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		super.init();
	}

	private class Seacher
	{
		
		private String appPath;

		public Seacher()
		{
			appPath = System.getProperty("appPath");
		} 
		
		public void createIndex() throws Exception
		{
			loggerServiceTracker.debug("Start creating Index");
			
			IndexWriter writer = new IndexWriter(FSDirectory.open(new File(appPath + "/shopSearchIndex/")), 
							getAnalyzer(), 
							true,
							MaxFieldLength.UNLIMITED);
			
			Shop[] allShop = shopDbhelper.getAllShop();
			for (Shop shop : allShop)
			{
				Document doc = new Document();
				doc.add(new Field("id", shop.getShopId() + "", Field.Store.YES, Field.Index.NO));
				doc.add(new Field("eusername", shop.getEusername(), Field.Store.NO, Field.Index.NOT_ANALYZED));
				doc.add(new Field("type", shop.getType(), Field.Store.NO, Field.Index.NOT_ANALYZED));
				doc.add(new Field("title", shop.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("content", shop.getContent(), Field.Store.NO, Field.Index.ANALYZED));
				doc.add(new Field("shopImg", shop.getShopImg(), Field.Store.YES, Field.Index.NO));
				doc.add(new Field("district", shop.getDistrict(), Field.Store.NO, Field.Index.ANALYZED));
				doc.add(new Field("street", shop.getStreet(), Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("tel", shop.getTel(), Field.Store.YES, Field.Index.NO));
				
				writer.addDocument(doc);
			}
			writer.optimize();
			writer.close();
			loggerServiceTracker.debug("Create Index completed");
		}
		
		private Analyzer getAnalyzer()
		{
			return new SmartChineseAnalyzer(Version.LUCENE_29);
		}
		
		public Object[] seachIndex(String queryString, int page, int count, boolean getTotal) throws CorruptIndexException, IOException, ParseException
		{
			Object[] result = new Object[2];
			IndexSearcher searcher = new IndexSearcher(FSDirectory.open(new File(appPath + "/luceneTest/")), false);
			Map<Integer, ScoreDoc> scoreDocs = new TreeMap<Integer, ScoreDoc>();
			
			String[] searchField = {"title", "content", "type", "street", "district", "eusername"};
			for (String field : searchField)
			{
				QueryParser queryParser = new QueryParser(Version.LUCENE_29, field, getAnalyzer());
				Query query = queryParser.parse(queryString);
				TopDocs topDocs = searcher.search(query, 10000);
				ScoreDoc[] hits = topDocs.scoreDocs;
				for (int i = 0; i < hits.length; i++)
				{
					ScoreDoc scoreDoc = hits[i];
					int docid = scoreDoc.doc;
					if (!scoreDocs.containsKey(docid))
					{
						scoreDocs.put(docid, scoreDoc);
					}
				}
			}
			
			int startIndex = (page - 1) * count;
			int currentIndex = 0;
			List<Shop> resultShops = new ArrayList<Shop>();
			for (ScoreDoc scoreDoc : scoreDocs.values())
			{
				if (currentIndex >= startIndex)
				{
					Document d = searcher.doc(scoreDoc.doc);
					String idStr = d.get("id");
					Shop shop = new Shop();
					shop.setShopId(Integer.parseInt(idStr));
					shop.setTitle(d.get("title"));
					shop.setShopImg(d.get("shopImg"));
					shop.setTel(d.get("tel"));
					shop.setStreet(d.get("street"));
					resultShops.add(shop);
					
					if (resultShops.size() >= count)
					{
						break;
					}
				}
				++currentIndex;
			}
			
			result[0] = scoreDocs.size();
			
			
			
			result[1] = resultShops.toArray(new Shop[]{});
			
			return result;
			
		}
	}
}
