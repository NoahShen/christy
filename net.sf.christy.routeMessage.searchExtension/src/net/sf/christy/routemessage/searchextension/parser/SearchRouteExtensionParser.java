package net.sf.christy.routemessage.searchextension.parser;


import net.sf.christy.routemessage.RouteExtension;
import net.sf.christy.routemessage.searchextension.CheckedNode;
import net.sf.christy.routemessage.searchextension.SearchRouteExtension;
import net.sf.christy.routemessage.searchextension.CheckedNode.BindedResouce;
import net.sf.christy.routemessageparser.RouteExtensionParser;
import net.sf.christy.routemessageparser.RouteMessageParser;
import net.sf.christy.xmpp.Presence;

import org.xmlpull.v1.XmlPullParser;

public class SearchRouteExtensionParser implements RouteExtensionParser
{
	public static final String ELEMENTNAME = "search";
	
	public static final String NAMESPACE = "christy:internal:searchResource";
	
	private XmppParserServiceTracker xmppParserServiceTracker;
	
	public SearchRouteExtensionParser(XmppParserServiceTracker xmppParserServiceTracker)
	{
		this.xmppParserServiceTracker = xmppParserServiceTracker;
	}

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
	public RouteExtension parseExtension(XmlPullParser parser, RouteMessageParser routeParser) throws Exception
	{
		int times = Integer.parseInt((parser.getAttributeValue("", "times")));
		int total = Integer.parseInt((parser.getAttributeValue("", "total")));
		String startNode = parser.getAttributeValue("", "startNode");
		String fromc2s = parser.getAttributeValue("", "fromc2s");
		
		SearchRouteExtension searchRouteExtension = new SearchRouteExtension(times, total, startNode, fromc2s);
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("checkedNode".equals(elementName))
				{
					CheckedNode node = parseCheckNode(parser);
					searchRouteExtension.addCheckedNode(node);
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("search".equals(elementName))
				{
					done = true;
				}
			}
		}
		
		return searchRouteExtension;
	}

	private CheckedNode parseCheckNode(XmlPullParser parser) throws Exception
	{
		String name = parser.getAttributeValue("", "name");
		CheckedNode node = new CheckedNode(name);
		
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("bindedResouce".equals(elementName))
				{
					parseBindedResource(node, parser);
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("search".equals(elementName))
				{
					done = true;
				}
			}
		}
		return node;
	}

	private BindedResouce parseBindedResource(CheckedNode node, XmlPullParser parser) throws Exception
	{
		String resName = parser.getAttributeValue("", "name");
		String relatedC2s = parser.getAttributeValue("", "relatedC2s");
		String streamId = parser.getAttributeValue("", "streamId");
		String sessionBindedStr = parser.getAttributeValue("", "sessionBinded");
		boolean sessionBinded = Boolean.valueOf(sessionBindedStr);
		
		
		BindedResouce resource = node.new BindedResouce(resName, relatedC2s, streamId, sessionBinded);
		node.addBindedResouce(resource);
		
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("presence".equals(elementName))
				{
					Presence presence = 
						(Presence) xmppParserServiceTracker.getParser().parseParser(parser);
					resource.setPresence(presence);
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("bindedResouce".equals(elementName))
				{
					done = true;
				}
			}
		}
		return resource;
	}

}
