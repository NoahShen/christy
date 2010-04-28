package com.google.code.christy.xmpp.resultsetmgr.parser;

import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.resultsetmgr.ResultSetExtension;
import com.google.code.christy.xmppparser.ExtensionParser;
import com.google.code.christy.xmppparser.XmppParser;

public class ResultSetExtensionParser implements ExtensionParser
{

	public static final String ELEMENTNAME = "set";

	public static final String NAMESPACE = "http://jabber.org/protocol/rsm";
	
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
		ResultSetExtension extension = new ResultSetExtension();
		
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("first".equals(elementName))
				{
					String indexStr = parser.getAttributeValue("", "index");
					if (indexStr != null)
					{
						extension.setFirstIndex(Integer.parseInt(indexStr));
					}
					extension.setFirst(parser.nextText());
				}
				else if ("last".equals(elementName))
				{
					extension.setLast(parser.nextText());
				}
				else if ("after".equals(elementName))
				{
					extension.setAfter(parser.nextText());
				}
				else if ("max".equals(elementName))
				{
					extension.setMax(Integer.parseInt(parser.nextText()));
				}
				else if ("count".equals(elementName))
				{
					extension.setCount(Integer.parseInt(parser.nextText()));
				}
				else if ("index".equals(elementName))
				{
					extension.setIndex(Integer.parseInt(parser.nextText()));
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

}
