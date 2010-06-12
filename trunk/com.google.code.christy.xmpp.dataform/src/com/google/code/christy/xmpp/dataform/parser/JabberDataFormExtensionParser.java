package com.google.code.christy.xmpp.dataform.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.dataform.DataForm;
import com.google.code.christy.xmpp.dataform.FormField;
import com.google.code.christy.xmpp.dataform.DataForm.Item;
import com.google.code.christy.xmpp.dataform.DataForm.ReportedData;
import com.google.code.christy.xmpp.dataform.FormField.Option;
import com.google.code.christy.xmppparser.ExtensionParser;
import com.google.code.christy.xmppparser.XmppParser;


/**
 * @author noah
 * 
 */
public class JabberDataFormExtensionParser implements ExtensionParser
{
	public static final String ELEMENTNAME = "x";
	
	public static final String NAMESPACE = "jabber:x:data";
	
	public JabberDataFormExtensionParser()
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
		DataForm dataForm = new DataForm();
		boolean done = false;

		String type = parser.getAttributeValue("", "type");
		dataForm.setType(DataForm.Type.valueOf(type));
		while (!done)
		{
			int eventType = parser.next();

			if (eventType == XmlPullParser.START_TAG)
			{
				if ("title".equals(parser.getName()))
				{
					dataForm.setTitle(parser.nextText());
				}
				else if ("instructions".equals(parser.getName()))
				{
					dataForm.addInstruction(parser.nextText());
				}
				else if ("field".equals(parser.getName()))
				{
					dataForm.addField(parseFormField(parser));
				}
				else if ("reported".equals(parser.getName()))
				{
					dataForm.setReportedData(parseReported(parser));
				}
				else if ("item".equals(parser.getName()))
				{
					dataForm.addItem(parseItem(parser));
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if (getElementName().equals(parser.getName()))
				{
					done = true;
				}
			}
		}

		return dataForm;
	}

	private Item parseItem(XmlPullParser parser2) throws XmlPullParserException, IOException
	{
		DataForm.Item item = new DataForm.Item();
		boolean done = false;
		while (!done)
		{
			int eventType = parser2.next();

			if (eventType == XmlPullParser.START_TAG)
			{
				if ("field".equals(parser2.getName()))
				{
					item.addField(parseFormField(parser2));
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("item".equals(parser2.getName()))
				{
					done = true;
				}
			}
		}
		return item;
	}

	private ReportedData parseReported(XmlPullParser parser2) throws XmlPullParserException, IOException
	{
		boolean done = false;
		DataForm.ReportedData reported = new DataForm.ReportedData();

		while (!done)
		{
			int eventType = parser2.next();

			if (eventType == XmlPullParser.START_TAG)
			{
				if ("field".equals(parser2.getName()))
				{
					reported.addField(parseFormField(parser2));
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("reported".equals(parser2.getName()))
				{
					done = true;
				}
			}
		}
		return reported;
	}

	private FormField parseFormField(XmlPullParser parser2) throws XmlPullParserException, IOException
	{
		boolean done = false;
		FormField formField = new FormField(parser2.getAttributeValue("", "var"));
		formField.setType(parser2.getAttributeValue("", "type"));
		formField.setLabel(parser2.getAttributeValue("", "label"));

		while (!done)
		{
			int eventType = parser2.next();

			if (eventType == XmlPullParser.START_TAG)
			{
				if ("desc".equals(parser2.getName()))
				{
					formField.setDescription(parser2.nextText());
				}
				else if ("required".equals(parser2.getName()))
				{
					formField.setRequired(true);
				}
				else if ("value".equals(parser2.getName()))
				{
					formField.addValue(parser2.nextText());
				}
				else if ("option".equals(parser2.getName()))
				{
					FormField.Option option = parseOption(parser2);
					formField.addOption(option);
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("field".equals(parser2.getName()))
				{
					done = true;
				}
			}
		}
		return formField;
	}

	private Option parseOption(XmlPullParser parser2) throws XmlPullParserException, IOException
	{
		FormField.Option option = null;

		boolean done = false;
		String label = parser2.getAttributeValue("", "label");

		while (!done)
		{
			int eventType = parser2.next();

			if (eventType == XmlPullParser.START_TAG)
			{
				if ("value".equals(parser2.getName()))
				{
					String value = parser2.nextText();
					option = new FormField.Option(label, value);
				}
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if ("option".equals(parser2.getName()))
				{
					done = true;
				}
			}
		}
		return option;
	}
}