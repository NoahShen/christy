package com.google.code.christy.routemessageparser.impl;

import java.io.StringReader;

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.routemessage.RouteExtension;
import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.routemessageparser.RouteExtensionParser;
import com.google.code.christy.routemessageparser.RouteMessageParser;


public class RouteMessageParserImpl implements RouteMessageParser
{
	private RouteExtensionParserServiceTracker routeExtensionParserServiceTracker;
	
	private XmppParserServiceTracker xmppParserServiceTracker;
	/**
	 * @param routeExtensionParserServiceTracker
	 * @param xmppParserServiceTracker 
	 */
	public RouteMessageParserImpl(RouteExtensionParserServiceTracker routeExtensionParserServiceTracker, 
								XmppParserServiceTracker xmppParserServiceTracker)
	{
		this.routeExtensionParserServiceTracker = routeExtensionParserServiceTracker;
		this.xmppParserServiceTracker = xmppParserServiceTracker;
	}

	@Override
	public RouteExtensionParser getRouteExtensionParser(String elementName, String namespace)
	{
		return routeExtensionParserServiceTracker.getRouteExtensionParser(elementName, namespace);
	}

	@Override
	public RouteMessage parseParser(XmlPullParser parser) throws Exception
	{
		String elementName = parser.getName();
		if ("route".equals(elementName))
		{
			return parseRoute(parser);	
		}
		
		return null;
	}

	private RouteMessage parseRoute(XmlPullParser parser) throws Exception
	{		
		String from = parser.getAttributeValue("", "from");
		String to = parser.getAttributeValue("", "to");
		String streamId = parser.getAttributeValue("", "streamid");
		String toUserNode = parser.getAttributeValue("", "toUserNode");
		RouteMessage routeMessage = new RouteMessage(from, to, streamId);
		routeMessage.setToUserNode(toUserNode);
		
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			
			if (eventType == XmlPullParser.START_TAG)
			{
				String namespace = parser.getNamespace(null);
				if (namespace == null)
				{
					namespace = parser.getAttributeValue(null, "xmlns");
				}
				if ("closeStream".equals(elementName))
				{
					routeMessage.setCloseStream(true);
				}
				else if ("iq".equals(elementName)
						|| "message".equals(elementName)
						|| "presence".equals(elementName)
						|| "error".equals(elementName)
						|| "stream:error".equals(elementName))
				{
					routeMessage.setXmlStanza(xmppParserServiceTracker.getParser().parseParser(parser));
				}
				else
				{
					processExtension(parser, elementName, namespace, routeMessage);
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("route".equals(elementName))
				{
					done = true;
				}
			}
		}
		
		return routeMessage;
	}

	private void processExtension(XmlPullParser parser, String elementName, String namespace, RouteMessage routeMessage) throws Exception
	{
		RouteExtensionParser xparser = routeExtensionParserServiceTracker.getRouteExtensionParser(elementName, namespace);
		if (xparser != null)
		{
			RouteExtension routeExtension = xparser.parseExtension(parser, this);			
			routeMessage.addRouteExtension(routeExtension);
		}
	}

	@Override
	public RouteMessage parseXml(String xml) throws Exception
	{
		
		XmlPullParser parser = new MXParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);

		StringReader strReader = new StringReader(xml);
		parser.setInput(strReader);

		try
		{
			parser.next();
		}
		catch (Exception e)
		{			
			return null;
		}

		return parseParser(parser);
	}

}
