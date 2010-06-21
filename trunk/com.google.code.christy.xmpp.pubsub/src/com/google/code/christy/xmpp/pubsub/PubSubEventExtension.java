package com.google.code.christy.xmpp.pubsub;

import java.util.ArrayList;
import java.util.List;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.XmlStanza;

public class PubSubEventExtension implements PacketExtension
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6180215351775174696L;

	public static final String ELEMENTNAME = "event";

	public static final String NAMESPACE = "http://jabber.org/protocol/pubsub#event";
	
	private List<XmlStanza> stanzas = new ArrayList<XmlStanza>();
	
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
	

	public List<XmlStanza> getStanzas()
	{
		return stanzas;
	}

	public void addStanza(XmlStanza stanza)
	{
		this.stanzas.add(stanza);
	}
	
	public void remove(XmlStanza stanza)
	{
		this.stanzas.remove(stanza);
	}

	@Override
	public String toXml()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<" + getElementName() + " " + "xmlns=\"" + getNamespace() + "\"");
		if (getStanzas() != null)
		{
			buf.append(">");
			for (XmlStanza stanza : getStanzas())
			{
				buf.append(stanza.toXml());
			}
			
			buf.append("</" + getElementName() + ">");
		}
		else
		{
			buf.append("/>");
		}
		
		
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PubSubEventExtension pubSubEvent = (PubSubEventExtension) super.clone();
		pubSubEvent.stanzas = new ArrayList<XmlStanza>();
		for (XmlStanza stanza : this.stanzas)
		{
			pubSubEvent.stanzas.add((XmlStanza) stanza.clone());
		}
		return pubSubEvent;
	}
}
