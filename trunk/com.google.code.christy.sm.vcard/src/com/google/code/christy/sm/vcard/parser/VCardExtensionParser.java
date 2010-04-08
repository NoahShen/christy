package com.google.code.christy.sm.vcard.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.code.christy.sm.vcard.VCardPacketExtension;
import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmppparser.ExtensionParser;
import com.google.code.christy.xmppparser.XmppParser;

/**
 * @author noah
 * 
 */
public class VCardExtensionParser implements ExtensionParser
{

	public static final String ELEMENTNAME = "vCard";

	public static final String NAMESPACE = "vcard-temp";

	public VCardExtensionParser()
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.mxl.mxlmainbundle.ExtensionParser#parseExtension(java.lang.Object)
	 */
	@Override
	public PacketExtension parseExtension(XmlPullParser parser, XmppParser xmppParser) throws Exception
	{
		VCardPacketExtension vCard = new VCardPacketExtension();

		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("FN".equals(elementName))
				{
					vCard.setFullName(parser.nextText());
				}
				else if ("N".equals(elementName))
				{
					parseN(vCard, parser);
				}
				else if ("NICKNAME".equals(elementName))
				{
					vCard.setNickName(parser.nextText());
				}
				else if ("URL".equals(elementName))
				{
					vCard.setUrl(parser.nextText());
				}
				else if ("BDAY".equals(elementName))
				{
					vCard.setBirthday(parser.nextText());
				}
				else if ("ORG".equals(elementName))
				{
					parseORG(vCard, parser);
				}
				else if ("TITLE".equals(elementName))
				{
					vCard.setTitle(parser.nextText());
				}
				else if ("ROLE".equals(elementName))
				{
					vCard.setRole(parser.nextText());
				}
				else if ("TEL".equals(elementName))
				{
					parseTEL(vCard, parser);
				}
				else if ("ADR".equals(elementName))
				{
					parseADR(vCard, parser);
				}
				else if ("EMAIL".equals(elementName))
				{
					parseEMAIL(vCard, parser);
				}
				else if ("JABBERID".equals(elementName))
				{
					vCard.setJabberID(parser.nextText());
				}
				else if ("DESC".equals(elementName))
				{
					vCard.setDescription(parser.nextText());
				}
				else if ("PHOTO".equals(elementName))
				{
					parsePHOTO(vCard, parser);
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{

				if ("vCard".equals(elementName))
				{
					done = true;
				}
			}

		}
		return vCard;
	}

	private void parsePHOTO(VCardPacketExtension card, XmlPullParser parser2) throws XmlPullParserException, IOException
	{
		boolean done = false;
		while (!done)
		{
			int eventType = parser2.next();
			String elementName = parser2.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("TYPE".equals(elementName))
				{
					card.setPhotoType(parser2.nextText());
				}
				else if ("BINVAL".equals(elementName))
				{
					card.setPhotoBinval(parser2.nextText());
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("PHOTO".equals(elementName))
				{
					done = true;
				}
			}
		}
	}

	private void parseEMAIL(VCardPacketExtension card, XmlPullParser parser2) throws XmlPullParserException, IOException
	{
		boolean done = false;
		while (!done)
		{
			int eventType = parser2.next();
			String elementName = parser2.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("USERID".equals(elementName))
				{
					card.setEmail(parser2.nextText());
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("EMAIL".equals(elementName))
				{
					done = true;
				}
			}
		}
	}

	private void parseADR(VCardPacketExtension card, XmlPullParser parser2) throws XmlPullParserException, IOException
	{
		boolean work = false;
		int eventType = parser2.nextTag();
		if ("WORK".equals(parser2.getName()))
		{
			work = true;
		}
		boolean done = false;
		while (!done)
		{
			eventType = parser2.next();
			String elementName = parser2.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if (work)
				{
					card.setWorkAddress(elementName, parser2.nextText());
				}
				else
				{
					card.setHomeAddress(elementName, parser2.nextText());
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("ADR".equals(elementName))
				{
					done = true;
				}
			}
		}
	}

	private void parseTEL(VCardPacketExtension card, XmlPullParser parser2) throws XmlPullParserException, IOException
	{
		boolean work = false;
		String phoneType = null;
		boolean done = false;
		while (!done)
		{
			int eventType = parser2.next();
			String elementName = parser2.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("WORK".equals(elementName))
				{
					work = true;
					int eType = parser2.nextTag();
					if (eType == XmlPullParser.START_TAG)
					{
						phoneType = parser2.getName();
					}
				}
				else if ("NUMBER".equals(elementName))
				{
					if (phoneType != null)
					{
						if (work)
						{
							card.setWorkPhone(phoneType, parser2.nextText());
						}
						else
						{
							card.setHomePhone(phoneType, parser2.nextText());
						}
					}
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("TEL".equals(elementName))
				{
					done = true;
				}
			}
		}
	}

	private void parseORG(VCardPacketExtension card, XmlPullParser parser2) throws XmlPullParserException, IOException
	{
		boolean done = false;
		while (!done)
		{
			int eventType = parser2.next();
			String elementName = parser2.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("ORGNAME".equals(elementName))
				{
					card.setOrganizationName(parser2.nextText());
				}
				else if ("ORGUNIT".equals(elementName))
				{
					card.setOrganizationUnit(parser2.nextText());
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("ORG".equals(elementName))
				{
					done = true;
				}
			}
		}
	}

	private void parseN(VCardPacketExtension card, XmlPullParser parser2) throws XmlPullParserException, IOException
	{

		boolean done = false;
		while (!done)
		{
			int eventType = parser2.next();
			String elementName = parser2.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("FAMILY".equals(elementName))
				{
					card.setFamilyName(parser2.nextText());
				}
				else if ("GIVEN".equals(elementName))
				{
					card.setGivenName(parser2.nextText());
				}
				else if ("MIDDLE".equals(elementName))
				{
					card.setMiddleName(parser2.nextText());
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("N".equals(elementName))
				{
					done = true;
				}
			}
		}
	}
}