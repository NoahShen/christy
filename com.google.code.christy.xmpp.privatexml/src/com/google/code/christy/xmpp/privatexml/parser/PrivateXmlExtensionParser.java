package com.google.code.christy.xmpp.privatexml.parser;

import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.privatexml.PrivateXmlExtension;
import com.google.code.christy.xmppparser.ExtensionParser;
import com.google.code.christy.xmppparser.UnknownPacketExtension;
import com.google.code.christy.xmppparser.XmppParser;

public class PrivateXmlExtensionParser implements ExtensionParser
{
	public static final String ELEMENTNAME = "query";

	public static final String NAMESPACE = "jabber:iq:private";
	
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
		PrivateXmlExtension extension = new PrivateXmlExtension();
		
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				String xmlns = parser.getNamespace(null);
				UnknownPacketExtension unknownX = xmppParser.parseUnknownExtension(parser, elementName, xmlns);
				extension.setUnknownPacketExtension(unknownX);
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
