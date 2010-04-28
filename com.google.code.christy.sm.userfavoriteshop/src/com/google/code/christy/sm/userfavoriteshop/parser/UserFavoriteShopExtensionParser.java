package com.google.code.christy.sm.userfavoriteshop.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.code.christy.sm.userfavoriteshop.UserFavoriteShopExtension;
import com.google.code.christy.sm.userfavoriteshop.UserFavoriteShopExtension.ShopItem;
import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.resultsetmgr.ResultSetExtension;
import com.google.code.christy.xmppparser.ExtensionParser;
import com.google.code.christy.xmppparser.XmppParser;

public class UserFavoriteShopExtensionParser implements ExtensionParser
{
	public static final String ELEMENTNAME = "shops";

	public static final String NAMESPACE = "christy:shop:user:favoriteshop";
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

	@Override
	public PacketExtension parseExtension(XmlPullParser parser, XmppParser xmppParser) throws Exception
	{
		UserFavoriteShopExtension extension = new UserFavoriteShopExtension();
		
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("shop".equals(elementName))
				{
					extension.addShopItem(parseShopItem(parser));
				}
				else if (ResultSetExtension.ELEMENTNAME.equals(elementName))
				{
					ExtensionParser exParser = xmppParser.getExtensionParser(ResultSetExtension.ELEMENTNAME, ResultSetExtension.NAMESPACE);
					if (exParser != null)
					{
						ResultSetExtension rsmx = (ResultSetExtension) exParser.parseExtension(parser, xmppParser);
						extension.setResultSetExtension(rsmx);
					}
					xmppParser.parseParser(parser);
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if (getElementName().equals(elementName))
				{
					done = true;
				}
			}
		}
		
		return extension;
	}

	private ShopItem parseShopItem(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String shopId = parser.getAttributeValue("", "id");
		String actionStr = parser.getAttributeValue("", "action");
		UserFavoriteShopExtension.ShopItem shopItem = 
			new UserFavoriteShopExtension.ShopItem(Long.parseLong(shopId));
		shopItem.setAction(UserFavoriteShopExtension.ShopAction.valueOf(actionStr));
		
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("name".equals(elementName))
				{
					shopItem.setShopName(parser.nextText());
				}
				else if ("street".equals(elementName))
				{
					shopItem.setStreet(parser.nextText());
				}
				else if ("tel".equals(elementName))
				{
					shopItem.setTel(parser.nextText());
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("shop".equals(elementName))
				{
					done = true;
				}
			}
		}
		
		return shopItem;
	}

}
