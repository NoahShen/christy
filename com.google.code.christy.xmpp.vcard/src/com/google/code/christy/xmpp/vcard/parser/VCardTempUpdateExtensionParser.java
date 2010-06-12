package com.google.code.christy.xmpp.vcard.parser;

import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.vcard.VCardTempUpdatePacketExtension;
import com.google.code.christy.xmppparser.ExtensionParser;
import com.google.code.christy.xmppparser.XmppParser;

/**
 * @author noah
 * 
 */
public class VCardTempUpdateExtensionParser implements ExtensionParser
{
	public static final String ELEMENTNAME = "x";

	public static final String NAMESPACE = "vcard-temp:x:update";

	public VCardTempUpdateExtensionParser()
	{
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
	public PacketExtension parseExtension(XmlPullParser parser, XmppParser xmppParser) throws Exception
	{
		VCardTempUpdatePacketExtension vCardTempUpdate = null;

		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("photo".equals(elementName))
				{
					vCardTempUpdate = new VCardTempUpdatePacketExtension(parser.nextText());
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

		return vCardTempUpdate;
	}

}