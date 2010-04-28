package com.google.code.christy.sm.userfavoriteshop;

import java.util.ArrayList;
import java.util.List;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.XmlStanza;


public class UserFavoriteShopExtension implements PacketExtension
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5858123534975211223L;

	public static final String ELEMENTNAME = "shops";

	public static final String NAMESPACE = "christy:shop:user:favoriteshop";
	
	private List<ShopItem> shopItems = new ArrayList<ShopItem>();
	
	@Override
	public String getElementName()
	{
		return ELEMENTNAME;
	}

	@Override
	public String getNamespace()
	{
		return NAMESPACE;
	}
	
	public void addShopItem(ShopItem item)
	{
		shopItems.add(item);
	}
	
	public void removeShopItem(ShopItem item)
	{
		shopItems.remove(item);
	}
	
	public ShopItem[] getShopItems()
	{
		return shopItems.toArray(new ShopItem[]{});
	}
	
	@Override
	public String toXml()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<" + getElementName() + " " + "xmlns=\"" + getNamespace() + "\"");
		
		if (shopItems.isEmpty())
		{
			buf.append("/>");
			
		}
		else
		{
			buf.append(">");

			for (ShopItem item: shopItems)
			{
				buf.append(item.toXml());
			}
			
			buf.append("</" + getElementName() + ">");
		}
		
		
		return buf.toString();
	}
	
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		UserFavoriteShopExtension userFavoriteShopExtension = (UserFavoriteShopExtension) super.clone();
		userFavoriteShopExtension.shopItems = new ArrayList<ShopItem>();
		for (ShopItem item: shopItems)
		{
			userFavoriteShopExtension.shopItems.add((ShopItem) item.clone());
		}
		return userFavoriteShopExtension;
	}
	
	public static class ShopItem implements XmlStanza
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8797249893946717669L;

		private long shopId;
		
		private ShopAction action;
		
		private String shopName;
		
		private String street;
		
		private String tel;
		
		public ShopItem(long shopId)
		{
			super();
			this.shopId = shopId;
		}

		public long getShopId()
		{
			return shopId;
		}

		public ShopAction getAction()
		{
			return action;
		}

		public void setAction(ShopAction action)
		{
			this.action = action;
		}

		public String getShopName()
		{
			return shopName;
		}

		public void setShopName(String shopName)
		{
			this.shopName = shopName;
		}

		public String getStreet()
		{
			return street;
		}

		public void setStreet(String street)
		{
			this.street = street;
		}

		public String getTel()
		{
			return tel;
		}

		public void setTel(String tel)
		{
			this.tel = tel;
		}

		@Override
		public String toXml()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("<shop id=\"" + getShopId() + "\"");
			if (getAction() != null)
			{
				buf.append(" action=\"").append(getAction().name()).append("\"");
			}
			buf.append(">");
			
			if (getShopName() != null)
			{
				buf.append("<name>").append(getShopName()).append("</name>");
			}
			
			if (getStreet() != null)
			{
				buf.append("<street>").append(getStreet()).append("</street>");
			}
			
			if (getTel() != null)
			{
				buf.append("<tel>").append(getTel()).append("</tel>");
			}
			
			buf.append("</shop>");
			return buf.toString();
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException
		{
			ShopItem shopItem = (ShopItem) super.clone();
			shopItem.shopId = this.shopId;
			shopItem.action = this.action;
			shopItem.shopName = this.shopName;
			shopItem.street =this.street;
			shopItem.tel =this.tel;
			return shopItem;
		}
		
	}
	
	public enum ShopAction
	{
		add,
		
		remove
	}
}
