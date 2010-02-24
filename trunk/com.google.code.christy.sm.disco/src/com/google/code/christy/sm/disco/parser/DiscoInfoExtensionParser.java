package com.google.code.christy.sm.disco.parser;


import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.sm.disco.DiscoInfoExtension;
import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmppparser.ExtensionParser;
import com.google.code.christy.xmppparser.XmppParser;

/**
 * @author noah
 * 
 */
public class DiscoInfoExtensionParser implements ExtensionParser
{

	@Override
	public String getElementName()
	{
		return "query";
	}

	@Override
	public String getNamespace()
	{
		return "http://jabber.org/protocol/disco#info";
	}

	@Override
	public PacketExtension parseExtension(XmlPullParser parser, XmppParser xmppParser) throws Exception
	{
		DiscoInfoExtension discoInfo = new DiscoInfoExtension();
		String node = parser.getAttributeValue("", "node");
		discoInfo.setNode(node);

		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();

			if (eventType == XmlPullParser.START_TAG)
			{
				String namespace = parser.getNamespace(null);
				if ("identity".equals(elementName))
				{
					String category = parser.getAttributeValue("", "category");
					String type = parser.getAttributeValue("", "type");
					String name = parser.getAttributeValue("", "name");
					discoInfo.addIdentity(category, type, name);
				}
				else if ("feature".equals(elementName))
				{
					String strfeature = parser.getAttributeValue("", "var");
					DiscoInfoExtension.Feature feature = new DiscoInfoExtension.Feature(strfeature);
					discoInfo.addFeature(feature);
				}
				else
				{
					ExtensionParser xparser = xmppParser.getExtensionParser(elementName, namespace);
					if (xparser != null)
					{
						PacketExtension packetX = xparser.parseExtension(parser, xmppParser);
						discoInfo.addExtension(packetX);
					}
					else
					{
						PacketExtension packetX = xmppParser
								.parseUnknownExtension(parser, elementName, namespace);
						discoInfo.addExtension(packetX);
					}
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
		return discoInfo;
	}

}