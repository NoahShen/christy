package net.sf.christy.router.consistenthashingbinder.parser;

import java.io.IOException;

import net.sf.christy.routemessage.RouteExtension;
import net.sf.christy.routemessageparser.RouteExtensionParser;
import net.sf.christy.routemessageparser.RouteMessageParser;
import net.sf.christy.router.consistenthashingbinder.CheckedNode;
import net.sf.christy.router.consistenthashingbinder.SearchRouteExtension;
import net.sf.christy.router.consistenthashingbinder.CheckedNode.BindedResouce;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SearchRouteExtensionParser implements RouteExtensionParser
{
	public static final String ELEMENTNAME = "search";
	
	public static final String NAMESPACE = "christy:internal:searchResource";
	
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

	private CheckedNode parseCheckNode(XmlPullParser parser) throws XmlPullParserException, IOException
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
					String resName = parser.getAttributeValue("", "name");
					String relatedC2s = parser.getAttributeValue("", "relatedC2s");
					
					BindedResouce resource = node.new BindedResouce(resName, relatedC2s);
					node.addBindedResouce(resource);
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

}
