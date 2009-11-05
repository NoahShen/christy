package net.sf.christy.routemessageparser.impl;

import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import net.sf.christy.routemessage.RouteExtension;
import net.sf.christy.routemessage.RouteMessage;
import net.sf.christy.routemessageparser.RouteExtensionParser;
import net.sf.christy.routemessageparser.RouteMessageParser;

public class RouteMessageParserImpl implements RouteMessageParser
{
	private final Logger logger = LoggerFactory.getLogger(RouteMessageParserImpl.class);
	
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
				if ("iq".equals(elementName)
						|| "message".equals(elementName)
						|| "present".equals(elementName))
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
			logger.debug("get [" + elementName + " " + namespace + "]ExtensionParser: " + xparser);
			
			RouteExtension routeExtension = xparser.parseExtension(parser, this);
			
			logger.debug("ExtensionParser parse extension complete:" + routeExtension);
			
			routeMessage.addRouteExtension(routeExtension);
		}
		else
		{
			logger.debug("can not get [" + elementName + " " + namespace + "]ExtensionParser");
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
			if (logger.isDebugEnabled())
			{
				e.printStackTrace();
				logger.debug("parse exception:" + e.getMessage());
			}
			
			return null;
		}

		return parseParser(parser);
	}

}
