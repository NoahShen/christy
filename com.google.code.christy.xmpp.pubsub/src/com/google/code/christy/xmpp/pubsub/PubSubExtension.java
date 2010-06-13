package com.google.code.christy.xmpp.pubsub;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.XmlStanza;

public class PubSubExtension implements PacketExtension
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7779285156614323471L;

	public static final String ELEMENTNAME = "pubsub";

	public static final String PUBSUB_NAMESPACE = "http://jabber.org/protocol/pubsub";
	
	private String namespace;
	
	private XmlStanza stanza;
	
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

	public XmlStanza getStanza()
	{
		return stanza;
	}

	public void setStanza(XmlStanza stanza)
	{
		this.stanza = stanza;
	}

	@Override
	public String toXml()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<" + getElementName() + " " + "xmlns=\"" + getNamespace() + "\"");
		if (getStanza() != null)
		{
			buf.append(">");
			buf.append(getStanza().toXml());
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
		pubSubExtension.stanza = (XmlStanza) this.stanza.clone();
		return pubSubExtension;
	}
}
