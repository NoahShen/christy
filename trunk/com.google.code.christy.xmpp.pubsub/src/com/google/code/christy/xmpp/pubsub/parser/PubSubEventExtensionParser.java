package com.google.code.christy.xmpp.pubsub.parser;

import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.pubsub.PubSubEventExtension;
import com.google.code.christy.xmpp.pubsub.PubSubItems;
import com.google.code.christy.xmppparser.ExtensionParser;
import com.google.code.christy.xmppparser.UnknownPacketExtension;
import com.google.code.christy.xmppparser.XmppParser;

public class PubSubEventExtensionParser implements ExtensionParser
{
	public static final String ELEMENTNAME = "event";

	public static final String NAMESPACE = "http://jabber.org/protocol/pubsub#event";
	
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
		PubSubEventExtension pubSubEventExtension = new PubSubEventExtension();
		
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				String namespace = parser.getNamespace(null);
				if ("items".equals(elementName))
				{
					pubSubEventExtension.addStanza(parseItems(parser, xmppParser));
				}
				else
				{
					pubSubEventExtension.addStanza(xmppParser.parseUnknownExtension(parser, elementName, namespace));
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
		
		return pubSubEventExtension;
	}


	private PubSubItems parseItems(XmlPullParser parser, XmppParser xmppParser) throws Exception
	{
		String node = parser.getAttributeValue("", "node");
		String maxItemsStr = parser.getAttributeValue("", "max_items");
		String subId = parser.getAttributeValue("", "subid");
		
		PubSubItems items = new PubSubItems(node);
		items.setSubId(subId);
		if (maxItemsStr != null)
		{
			items.setMaxItems(Integer.parseInt(maxItemsStr));
		}
		
		
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("item".equals(elementName))
				{
					String id = parser.getAttributeValue("", "id");
					PubSubItems.Item item = new PubSubItems.Item(id);
					UnknownPacketExtension payload = parseItemContent(parser, xmppParser);
					if (payload != null)
					{
						item.setPayload(payload.toXml());
					}
					
					items.addItem(item);
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("items".equals(elementName))
				{
					done = true;
				}
			}
		}
		return items;
	}
	

	private UnknownPacketExtension parseItemContent(XmlPullParser parser, XmppParser xmppParser) throws Exception
	{
		UnknownPacketExtension unknownX = null;
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				String xmlns = parser.getNamespace(null);
				unknownX = xmppParser.parseUnknownExtension(parser, elementName, xmlns);
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("item".equals(elementName))
				{
					done = true;
				}
			}
		}
		return unknownX;
	}

}
