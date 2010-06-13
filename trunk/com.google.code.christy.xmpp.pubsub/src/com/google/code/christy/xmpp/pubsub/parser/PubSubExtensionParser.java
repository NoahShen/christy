package com.google.code.christy.xmpp.pubsub.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.pubsub.PubSubAffiliations;
import com.google.code.christy.xmpp.pubsub.PubSubExtension;
import com.google.code.christy.xmpp.pubsub.PubSubSubscriptions;
import com.google.code.christy.xmppparser.ExtensionParser;
import com.google.code.christy.xmppparser.XmppParser;

public class PubSubExtensionParser implements ExtensionParser
{
	public static final String ELEMENTNAME = "pubsub";

	public static final String NAMESPACE = "http://jabber.org/protocol/pubsub";
	
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
		String ns = parser.getNamespace(null);
		PubSubExtension extension = new PubSubExtension(ns);
		
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("subscriptions".equals(elementName))
				{
					extension.setStanza(parseSubscription(parser));
				}
				else if ("affiliations".equals(elementName))
				{
					extension.setStanza(parseAffiliation(parser));
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

	private PubSubAffiliations parseAffiliation(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String node = parser.getAttributeValue("", "node");
		
		PubSubAffiliations pubSubAffiliations = new PubSubAffiliations();
		pubSubAffiliations.setNode(node);
		
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("affiliation".equals(elementName))
				{
					String subNode = parser.getAttributeValue("", "node");
					String affiliationTypeStr = parser.getAttributeValue("", "affiliation");
					PubSubAffiliations.Affiliation affiliation = 
						new PubSubAffiliations.Affiliation(subNode, PubSubAffiliations.AffiliationType.valueOf(affiliationTypeStr));
					
					pubSubAffiliations.addAffiliation(affiliation);
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("affiliations".equals(elementName))
				{
					done = true;
				}
			}
		}
		
		return pubSubAffiliations;
	}

	private PubSubSubscriptions parseSubscription(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String node = parser.getAttributeValue("", "node");
		
		PubSubSubscriptions pubSubSubscriptions = new PubSubSubscriptions();
		pubSubSubscriptions.setNode(node);
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if ("subscription".equals(elementName))
				{
					String subNode = parser.getAttributeValue("", "node");
					String jid = parser.getAttributeValue("", "jid");
					String subscription = parser.getAttributeValue("", "subscription");
					String subId = parser.getAttributeValue("", "subid");
					PubSubSubscriptions.Subscription sub = 
						new PubSubSubscriptions.Subscription(subNode, jid, PubSubSubscriptions.SubscriptionType.valueOf(subscription));
					sub.setSubId(subId);
					
					pubSubSubscriptions.addSubscription(sub);
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("subscriptions".equals(elementName))
				{
					done = true;
				}
			}
		}
		
		return pubSubSubscriptions;
	}

}
