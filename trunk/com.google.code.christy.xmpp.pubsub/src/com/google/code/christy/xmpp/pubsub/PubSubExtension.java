package com.google.code.christy.xmpp.pubsub;

import java.util.ArrayList;
import java.util.List;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.XmlStanza;

public class PubSubExtension implements PacketExtension
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7779285156614323471L;

	public static final String ELEMENTNAME = "pubsub";

	public static final String NAMESPACE = "http://jabber.org/protocol/pubsub";
	
	private String namespace;
	
	private List<XmlStanza> stanzas = new ArrayList<XmlStanza>();
	
	public PubSubExtension(String namespace)
	{
		super();
		this.namespace = namespace;
	}

	@Override
	public String getElementName()
	{
		return ELEMENTNAME;
	}

	@Override
	public String getNamespace()
	{
		return namespace;
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
		PubSubExtension pubSubExtension = (PubSubExtension) super.clone();
		pubSubExtension.namespace = this.namespace;
		pubSubExtension.stanzas = new ArrayList<XmlStanza>();
		for (XmlStanza stanza : this.stanzas)
		{
			pubSubExtension.stanzas.add((XmlStanza) stanza.clone());
		}
		return pubSubExtension;
	}
}
