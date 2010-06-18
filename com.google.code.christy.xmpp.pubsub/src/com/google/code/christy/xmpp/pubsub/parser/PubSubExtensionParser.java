package com.google.code.christy.xmpp.pubsub.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.XmlStanza;
import com.google.code.christy.xmpp.dataform.DataForm;
import com.google.code.christy.xmpp.pubsub.PubSubAffiliations;
import com.google.code.christy.xmpp.pubsub.PubSubExtension;
import com.google.code.christy.xmpp.pubsub.PubSubOptions;
import com.google.code.christy.xmpp.pubsub.PubSubSubscribe;
import com.google.code.christy.xmpp.pubsub.PubSubSubscriptionItem;
import com.google.code.christy.xmpp.pubsub.PubSubSubscriptions;
import com.google.code.christy.xmpp.pubsub.PubSubUnsubscribe;
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
				String namespace = parser.getNamespace(null);
				if ("subscriptions".equals(elementName))
				{
					extension.addStanza(parseSubscription(parser));
				}
				else if ("affiliations".equals(elementName))
				{
					extension.addStanza(parseAffiliation(parser));
				}
				else if ("subscribe".equals(elementName))
				{
					extension.addStanza(parseSubscribe(parser));
				}
				else if ("unsubscribe".equals(elementName))
				{
					extension.addStanza(parseUnsubscribe(parser));
				}
				else if ("options".equals(elementName))
				{
					extension.addStanza(parseOptions(parser, xmppParser));
				}
				else
				{
					extension.addStanza(xmppParser.parseUnknownExtension(parser, elementName, namespace));
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

	private XmlStanza parseOptions(XmlPullParser parser, XmppParser xmppParser) throws Exception
	{
		String node = parser.getAttributeValue("", "node");
		String jidStr = parser.getAttributeValue("", "jid");
		String subId = parser.getAttributeValue("", "subid");
		
		PubSubOptions pubSubOptions = new PubSubOptions();
		pubSubOptions.setNode(node);
		if (jidStr != null)
		{
			pubSubOptions.setJid(new JID(jidStr));
		}
		
		pubSubOptions.setSubId(subId);
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				String namespace = parser.getNamespace(null);
				if ("x".equals(elementName))
				{
					ExtensionParser xparser = xmppParser.getExtensionParser(elementName, namespace);
					if (xparser != null)
					{
						DataForm dataForm = (DataForm) xparser.parseExtension(parser, xmppParser);
						pubSubOptions.setDataForm(dataForm);
					}
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("options".equals(elementName))
				{
					done = true;
				}
			}
		}
		return pubSubOptions;
	}

	private PubSubUnsubscribe parseUnsubscribe(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String node = parser.getAttributeValue("", "node");
		String jidStr = parser.getAttributeValue("", "jid");
		String subId = parser.getAttributeValue("", "subid");
		
		PubSubUnsubscribe pubSubUnsubscribe = new PubSubUnsubscribe(node, new JID(jidStr));
		pubSubUnsubscribe.setSubId(subId);
		
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("unsubscribe".equals(elementName))
				{
					done = true;
				}
			}
		}
		return pubSubUnsubscribe;
	}

	private PubSubSubscribe parseSubscribe(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String node = parser.getAttributeValue("", "node");
		String jidStr = parser.getAttributeValue("", "jid");
		
		PubSubSubscribe pubSubSubscribe = new PubSubSubscribe(node, new JID(jidStr));

		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("subscribe".equals(elementName))
				{
					done = true;
				}
			}
		}
		return pubSubSubscribe;
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
					PubSubSubscriptionItem sub = 
						new PubSubSubscriptionItem(subNode, jid, PubSubSubscriptionItem.SubscriptionType.valueOf(subscription));
					sub.setSubId(subId);
					
					pubSubSubscriptions.addPubSubSubscription(sub);
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
